package com.fasterxml.jackson.databind.deser;

import java.util.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.ValueInjector;
import com.fasterxml.jackson.databind.introspect.*;
import com.fasterxml.jackson.databind.util.Annotations;

/**
 * Builder class used for aggregating deserialization information about
 * a POJO, in order to build a {@link JsonDeserializer} for deserializing
 * instances.
 */
public class BeanDeserializerBuilder
{
    /*
    /**********************************************************
    /* Configuration
    /**********************************************************
     */

    final protected DeserializationConfig _config;

    /**
     * @since 2.9
     */
    final protected DeserializationContext _context;

    /*
    /**********************************************************
    /* General information about POJO
    /**********************************************************
     */

    /**
     * Introspected information about POJO for deserializer to handle
     */
    final protected BeanDescription _beanDesc;

    /*
    /**********************************************************
    /* Accumulated information about properties
    /**********************************************************
     */
    
    /**
     * Properties to deserialize collected so far.
     */
    final protected Map<String, SettableBeanProperty> _properties
        = new LinkedHashMap<String, SettableBeanProperty>();
    
    /**
     * Value injectors for deserialization
     */
    protected List<ValueInjector> _injectables;
    
    /**
     * Back-reference properties this bean contains (if any)
     */
    protected HashMap<String, SettableBeanProperty> _backRefProperties;

    /**
     * Set of names of properties that are recognized but are to be ignored for deserialization
     * purposes (meaning no exception is thrown, value is just skipped).
     */
    protected HashSet<String> _ignorableProps;
    
    /**
     * Object that will handle value instantiation for the bean type.
     */
    protected ValueInstantiator _valueInstantiator;

    /**
     * Handler for Object Id values, if Object Ids are enabled for the
     * bean type.
     */
    protected ObjectIdReader _objectIdReader;
    
    /**
     * Fallback setter used for handling any properties that are not
     * mapped to regular setters. If setter is not null, it will be
     * called once for each such property.
     */
    protected SettableAnyProperty _anySetter;

    /**
     * Flag that can be set to ignore and skip unknown properties.
     * If set, will not throw an exception for unknown properties.
     */
    protected boolean _ignoreAllUnknown;

    /**
     * When creating Builder-based deserializers, this indicates
     * method to call on builder to finalize value.
     */
    protected AnnotatedMethod _buildMethod;

    /**
     * In addition, Builder may have additional configuration
     */
    protected JsonPOJOBuilder.Value _builderConfig;

    /*
    /**********************************************************
    /* Life-cycle: construction
    /**********************************************************
     */
    
    public BeanDeserializerBuilder(BeanDescription beanDesc,
            DeserializationContext ctxt)
    { 
        _beanDesc = beanDesc;
        _context = ctxt;
        _config = ctxt.getConfig();
    }

    /**
     * Copy constructor for sub-classes to use, when constructing
     * custom builder instances
     */
    protected BeanDeserializerBuilder(BeanDeserializerBuilder src)
    {
        _beanDesc = src._beanDesc;
        _context = src._context;
        _config = src._config;

        // let's make copy of properties
        _properties.putAll(src._properties);
        _injectables = _copy(src._injectables);
        _backRefProperties = _copy(src._backRefProperties);
        // Hmmh. Should we create defensive copies here? For now, not yet
        _ignorableProps = src._ignorableProps;        
        _valueInstantiator = src._valueInstantiator;
        _objectIdReader = src._objectIdReader;
        
        _anySetter = src._anySetter;
        _ignoreAllUnknown = src._ignoreAllUnknown;
        
        _buildMethod = src._buildMethod;
        _builderConfig = src._builderConfig;
    }

    private static HashMap<String, SettableBeanProperty> _copy(HashMap<String, SettableBeanProperty> src) {
        return (src == null) ? null
                : new HashMap<String, SettableBeanProperty>(src);
    }

    private static <T> List<T> _copy(List<T> src) {
        return (src == null) ? null : new ArrayList<T>(src);
    }

    /*
    /**********************************************************
    /* Life-cycle: state modification (adders, setters)
    /**********************************************************
     */

    /**
     * Method for adding a new property or replacing a property.
     */
    public void addOrReplaceProperty(SettableBeanProperty prop, boolean allowOverride) {
        _properties.put(prop.getName(), prop);
    }

    /**
     * Method to add a property setter. Will ensure that there is no
     * unexpected override; if one is found will throw a
     * {@link IllegalArgumentException}.
     */
    public void addProperty(SettableBeanProperty prop)
    {
        SettableBeanProperty old =  _properties.put(prop.getName(), prop);
        if (old != null && old != prop) { // should never occur...
            throw new IllegalArgumentException("Duplicate property '"+prop.getName()+"' for "+_beanDesc.getType());
        }
    }

    /**
     * Method called to add a property that represents so-called back reference;
     * reference that "points back" to object that has forward reference to
     * currently built bean.
     */
    public void  addBackReferenceProperty(String referenceName, SettableBeanProperty prop)
    {
        if (_backRefProperties == null) {
            _backRefProperties = new HashMap<String, SettableBeanProperty>(4);
        }
        // 15-Sep-2016, tatu: For some reason fixing access at point of `build()` does
        //    NOT work (2 failing unit tests). Not 100% clear why, but for now force
        //    access set early; unfortunate, but since it works....
        prop.fixAccess(_config);
        _backRefProperties.put(referenceName, prop);
        // also: if we had property with same name, actually remove it
        if (_properties != null) {
            _properties.remove(prop.getName());
        }
        // ??? 23-Jul-2012, tatu: Should it be included in list of all properties?
        //   For now, won't add, since it is inferred, not explicit...
    }

    public void addInjectable(PropertyName propName, JavaType propType,
            Annotations contextAnnotations, AnnotatedMember member,
            Object valueId)
    {
        if (_injectables == null) {
            _injectables = new ArrayList<ValueInjector>();
        }
        boolean fixAccess = _config.canOverrideAccessModifiers();
        boolean forceAccess = fixAccess && _config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);
        if (fixAccess) {
            member.fixAccess(forceAccess);
        }
        _injectables.add(new ValueInjector(propName, propType,
                contextAnnotations, member, valueId));
    }

    /**
     * Method that will add property name as one of properties that can
     * be ignored if not recognized.
     */
    public void addIgnorable(String propName)
    {
        if (_ignorableProps == null) {
            _ignorableProps = new HashSet<String>();
        }
        _ignorableProps.add(propName);
    }

    /**
     * Method called by deserializer factory, when a "creator property"
     * (something that is passed via constructor- or factory method argument;
     * instead of setter or field).
     *<p>
     * Default implementation does not do anything; we may need to revisit this
     * decision if these properties need to be available through accessors.
     * For now, however, we just have to ensure that we don't try to resolve
     * types that masked setter/field has (see [JACKSON-700] for details).
     */
    public void addCreatorProperty(SettableBeanProperty prop)
    {
        addProperty(prop);
    }

    public void setAnySetter(SettableAnyProperty s)
    {
        if (_anySetter != null && s != null) {
            throw new IllegalStateException("_anySetter already set to non-null");
        }
        _anySetter = s;
    }

    public void setIgnoreUnknownProperties(boolean ignore) {
        _ignoreAllUnknown = ignore;
    }

    public void setValueInstantiator(ValueInstantiator inst) {
        _valueInstantiator = inst;
    }

    public void setObjectIdReader(ObjectIdReader r) {
        _objectIdReader = r;
    }

    public void setPOJOBuilder(AnnotatedMethod buildMethod, JsonPOJOBuilder.Value config) {
        _buildMethod = buildMethod;
        _builderConfig = config;
    }
    
    /*
    /**********************************************************
    /* Public accessors
    /**********************************************************
     */
    
    /**
     * Method that allows accessing all properties that this
     * builder currently contains.
     *<p>
     * Note that properties are returned in order that properties
     * are ordered (explictly, or by rule), which is the serialization
     * order.
     */
    public Iterator<SettableBeanProperty> getProperties() {
        return _properties.values().iterator();
    }

    public SettableBeanProperty findProperty(PropertyName propertyName) {
        return _properties.get(propertyName.getSimpleName());
    }

    public boolean hasProperty(PropertyName propertyName) {
        return findProperty(propertyName) != null;
    }

    public SettableBeanProperty removeProperty(PropertyName name) {
        return _properties.remove(name.getSimpleName());
    }

    public SettableAnyProperty getAnySetter() {
        return _anySetter;
    }
    
    public ValueInstantiator getValueInstantiator() {
        return _valueInstantiator;
    }

    public List<ValueInjector> getInjectables() {
        return _injectables;
    }

    public ObjectIdReader getObjectIdReader() {
        return _objectIdReader;
    }

    public AnnotatedMethod getBuildMethod() {
    	return _buildMethod;
    }

    public JsonPOJOBuilder.Value getBuilderConfig() {
        return _builderConfig;
    }

    /*
    /**********************************************************
    /* Build method(s)
    /**********************************************************
     */

    /**
     * Method for constructing a {@link BeanDeserializer}, given all
     * information collected.
     */
    public JsonDeserializer<?> build()
    {
        Collection<SettableBeanProperty> props = _properties.values();
        _fixAccess(props);

        BeanPropertyMap propertyMap = BeanPropertyMap.construct(props,
                _config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
        propertyMap.assignIndexes();

        // view processing must be enabled if:
        // (a) fields are not included by default (when deserializing with view), OR
        // (b) one of properties has view(s) to included in defined
        boolean anyViews = !_config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (!anyViews) {
            for (SettableBeanProperty prop : props) {
                if (prop.hasViews()) {
                    anyViews = true;
                    break;
                }
            }
        }

        // one more thing: may need to create virtual ObjectId property:
        if (_objectIdReader != null) {
            /* 18-Nov-2012, tatu: May or may not have annotations for id property;
             *   but no easy access. But hard to see id property being optional,
             *   so let's consider required at this point.
             */
            ObjectIdValueProperty prop = new ObjectIdValueProperty(_objectIdReader, PropertyMetadata.STD_REQUIRED);
            propertyMap = propertyMap.withProperty(prop);
        }

        return new BeanDeserializer(this,
                _beanDesc, propertyMap, _backRefProperties, _ignorableProps, _ignoreAllUnknown,
                anyViews);
    }

    /**
     * Alternate build method used when we must be using some form of
     * abstract resolution, usually by using addition Type Id
     * ("polymorphic deserialization")
     * 
     * @since 2.0
     */
    public AbstractDeserializer buildAbstract() {
        return new AbstractDeserializer(this, _beanDesc, _backRefProperties);
    }

    /**
     * Method for constructing a specialized deserializer that uses
     * additional external Builder object during data binding.
     */
    public JsonDeserializer<?> buildBuilderBased(JavaType valueType, String expBuildMethodName)
        throws JsonMappingException
    {
        // First: validation; must have build method that returns compatible type
        if (_buildMethod == null) {
            // as per [databind#777], allow empty name
            if (!expBuildMethodName.isEmpty()) {
                _context.reportBadDefinition(_beanDesc.getType(),
                        String.format("Builder class %s does not have build method (name: '%s')",
                        _beanDesc.getBeanClass().getName(),
                        expBuildMethodName));
            }
        } else {
            // also: type of the method must be compatible
            Class<?> rawBuildType = _buildMethod.getRawReturnType();
            Class<?> rawValueType = valueType.getRawClass();
            if ((rawBuildType != rawValueType)
                    && !rawBuildType.isAssignableFrom(rawValueType)
                    && !rawValueType.isAssignableFrom(rawBuildType)) {
                _context.reportBadDefinition(_beanDesc.getType(),
                        String.format("Build method '%s' has wrong return type (%s), not compatible with POJO type (%s)",
                        _buildMethod.getFullName(),
                        rawBuildType.getName(),
                        valueType.getRawClass().getName()));
            }
        }
        // And if so, we can try building the deserializer
        Collection<SettableBeanProperty> props = _properties.values();
        _fixAccess(props);
        BeanPropertyMap propertyMap = BeanPropertyMap.construct(props,
                _config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
        propertyMap.assignIndexes();

        boolean anyViews = !_config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);

        if (!anyViews) {
            for (SettableBeanProperty prop : props) {
                if (prop.hasViews()) {
                    anyViews = true;
                    break;
                }
            }
        }

        if (_objectIdReader != null) {
            // May or may not have annotations for id property; but no easy access.
            // But hard to see id property being optional, so let's consider required at this point.
            ObjectIdValueProperty prop = new ObjectIdValueProperty(_objectIdReader,
                    PropertyMetadata.STD_REQUIRED);
            propertyMap = propertyMap.withProperty(prop);
        }

        return new BuilderBasedDeserializer(this,
                _beanDesc, valueType, propertyMap, _backRefProperties, _ignorableProps, _ignoreAllUnknown,
                anyViews);
    }

    /*
    /**********************************************************
    /* Internal helper method(s)
    /**********************************************************
     */

    private void _fixAccess(Collection<SettableBeanProperty> mainProps)
    {
        /* 07-Sep-2016, tatu: Ideally we should be able to avoid forcing
         *   access to properties that are likely ignored, but due to
         *   renaming it seems this is not a safe thing to do (there was
         *   at least one failing test). May need to dig deeper in future;
         *   for now let's just play it safe.
         */
        /*
        Set<String> ignorable = _ignorableProps;
        if (ignorable == null) {
            ignorable = Collections.emptySet();
        }
        */
        for (SettableBeanProperty prop : mainProps) {
            /*
            // first: no point forcing access on to-be-ignored properties
            if (ignorable.contains(prop.getName())) {
                continue;
            }
            */
            prop.fixAccess(_config);
        }
        // 15-Sep-2016, tatu: Access via back-ref properties has been done earlier
        //   as it has to, for some reason, so not repeated here.
/*        
        if (_backRefProperties != null) {
            for (SettableBeanProperty prop : _backRefProperties.values()) {
                prop.fixAccess(_config);
            }
        }
        */
        if (_anySetter != null) {
            _anySetter.fixAccess(_config);
        }
        if (_buildMethod != null) {
            _buildMethod.fixAccess(_config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
    }
}