package com.wx.yamlandproperties;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author wuxin
 * @date 2025/08/20 23:18:38
 */
public class YamlAndProperties {


    /**
     * 转换yaml数组到properties
     */
    private static final String PROP_LIST_TEMP = "[%s]";

    public static Map<String,Object> yaml2FlapMap(InputStream fileInputStream){
        try (fileInputStream) {
            Yaml yaml = new Yaml();
            Object load = yaml.load(fileInputStream);
            Map<String, Object> result = new LinkedHashMap<>();
            flapYamlObject(null, load, result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void flapYamlObject(String prefix,Object o, Map<String, Object> result) {
        if(o instanceof Map map){
            map.forEach((k,v)->{
                flapYamlObject(Optional.ofNullable(prefix).map(e->e+".").orElse("") +k.toString(), v, result);
            });
            return;
        }
        if(o instanceof List<?> list){
            IntStream.range(0, list.size()).forEach(e->{
                Object o1 = list.get(e);
                result.put(prefix+String.format(PROP_LIST_TEMP, e), o1);
            });
            return;
        }
        result.put(prefix, o);
    }



    public static Map<String, Object> propertiesLoad(InputStream inputStream){
        Properties properties = new Properties();
        try (inputStream){
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.entrySet().stream().collect(HashMap::new,(m, e)-> m.put(e.getKey().toString(),e.getValue()), Map::putAll);
    }



    public static void yamlMustAllInPropertiesAndValueMustEquals(InputStream yaml ,InputStream properties){
        Map<String, Object> yaml2FlapMap = yaml2FlapMap(yaml);
        Map<String, Object> propertiesLoad = propertiesLoad(properties);
        List<String> diff = yaml2FlapMap.entrySet().stream().collect(ArrayList::new, (container, e) -> {
            String k = e.getKey();
            Object v = e.getValue();
            if (!propertiesLoad.containsKey(k)) {
                container.add("key不存在于properties中: " + k);
                return;
            }
            Object o = propertiesLoad.get(k);
            // 一个为null 一个不为null
            // 两个都不为nullString化对比
            if((Objects.isNull(o) ^ Objects.isNull(v)) || (Objects.nonNull(o) && Objects.nonNull(v)  && !o.toString().equals(v.toString()))){
                container.add(String.format("key在yaml和properties中不等 (%s, %s) != (%s, %s)", k, v, k, o));
            }
        }, (a, b) -> a.addAll(b));
        diff.forEach(System.out::println);
    }



    public static void convertPropertiesToYaml(InputStream properties, OutputStream yamlOutPutStream) throws Exception {
        Properties propertiesAsPOJO = new Properties();
        try (properties){
            propertiesAsPOJO.load(properties);
            Map<String, Object> yamlMap = new LinkedHashMap<>();
            propertiesAsPOJO.forEach((key, value)->{
                String[] split = key.toString().split("\\.");
                Map<String, Object> yamlMapTemp = yamlMap;
                for (int i = 0; i < split.length; i++) {
                    String keyPart = split[i];
                    Object o = yamlMapTemp.get(keyPart);
                    if(Objects.isNull(o)){
                        if(i == split.length - 1){
                            yamlMapTemp.put(keyPart, value);
                        }else {
                            Map<String, Object> objectObjectHashMap = new LinkedHashMap<>();
                            yamlMapTemp.put(keyPart, objectObjectHashMap);
                            yamlMapTemp = objectObjectHashMap;
                        }
                    }else {
                        if(!(o instanceof Map)){
                            throw new RuntimeException("配置文件值重复！");
                        }
                        Map o1 = (Map) o;
                        if(i == split.length - 1 ){
                            o1.put(keyPart,value);
                        }
                        yamlMapTemp = o1;
                    }
                }
            });
            yamlArrayProcess(null, null, yamlMap);

            DumperOptions options = new DumperOptions();
            // 强制锁进
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);

            Yaml yaml = new Yaml(options);
            yaml.dump(yamlMap, new PrintWriter(yamlOutPutStream));
            yamlOutPutStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            yamlOutPutStream.close();
        }
    }


    private static void yamlArrayProcess(Map<String, Object> lastOne,String lastKey, Map<String, Object> o){
        Pattern compile = Pattern.compile("\\[\\d+\\]|\\d+");
        if(o.keySet().stream().anyMatch(e->compile.matcher(e).matches() ) &&  Objects.nonNull(lastKey) && Objects.nonNull(lastKey)){
            lastOne.put(lastKey, o.values().stream().collect(Collectors.toList()));
        }else {
            o.forEach((k,v)->{
                if(v instanceof Map<?,?>){
                    yamlArrayProcess(o, k, (Map<String, Object>)v);
                }
            });
        }
    }

    public static void main(String[] args) {
//        InputStream aYaml = YamlAndProperties.class.getClassLoader().getResourceAsStream("sllerCore.yaml");
//        InputStream bProper = YamlAndProperties.class.getClassLoader().getResourceAsStream("demeter.properties");
//        yamlMustAllInPropertiesAndValueMustEquals(aYaml, bProper);
    }
}