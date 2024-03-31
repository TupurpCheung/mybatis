package cn.tupurpcheung.mybatis.constants;

import java.util.regex.Pattern;

public interface XsdConstants {

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
