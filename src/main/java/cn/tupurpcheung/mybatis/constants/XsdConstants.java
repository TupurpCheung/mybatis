package cn.tupurpcheung.mybatis.constants;

import java.util.regex.Pattern;

public interface XsdConstants {



    /**
     * config.xml
     * */
    String ENVIRONMENTS_ELEMENT_NAME = "environments";
    String ENVIRONMENT_ELEMENT_NAME = "environment";
    String DEFAULT_ATTRIBUTE_NAME = "default";


    String DATASOURCE_ELEMENT_NAME = "dataSource";
    String TYPE_ATTRIBUTE_NAME = "type";

    String PROPERTY_ELEMENT_NAME = "property";
    String PROPERTIES_NAME_KEY  = "name";
    String PROPERTIES_VALUE_KEY  = "value";

    String TRANSACTION_MANAGER_ELEMENT_NAME = "transactionManager";



    /*
    *
    * mapper.xml
    * */
    String MAPPERS_ELEMENT_NAME = "mappers";
    String MAPPER_ELEMENT_NAME = "mapper";
    String RESOURCE_ATTRIBUTE_NAME = "resource";
    String NAMESPACE_ATTRIBUTE_NAME = "namespace";
    String SELECT_ELEMENT_NAME = "select";
    String ID_ATTRIBUTE_NAME = "id";
    String PARAMETER_TYPE_ATTRIBUTE_NAME = "parameterType";
    String RESULT_TYPE_ATTRIBUTE_NAME = "resultType";
    String PARAMETER_REGEX = "(#\\{(.*?)})";

    Pattern PARAMETER_PATTERN = Pattern.compile(PARAMETER_REGEX);

    String PARAMETER_PLACEHOLDER = "?";
}
