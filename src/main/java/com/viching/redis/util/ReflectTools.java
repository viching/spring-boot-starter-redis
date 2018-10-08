package com.viching.redis.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

public class ReflectTools {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<Object> adapter(JoinPoint joinPoint, Class clazz) throws Exception {
        List<Object> result = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        //获得参数列表
        Object[] arguments = joinPoint.getArgs();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
 
        //获取注解
        result.add(method.getAnnotation(clazz));
        //获取方法参数
        ParameterNameDiscoverer pnd = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = pnd.getParameterNames(method);//返回的是方法中的参数名列表
        for(int i = 0; i<paramNames.length;i++){
            parameters.put(paramNames[i], arguments[i]);
        }
        result.add(parameters);
        return result;
    }
    
    /**
     * 利用反射获取指定对象的指定属性
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        Object result = null;
        final Field field = ReflectTools.getField(obj, fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                result = field.get(obj);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 利用反射获取指定对象里面的指定属性
     */
    @SuppressWarnings("rawtypes")
    private static Field getField(Object obj, String fieldName) {
        Field field = null;
        for (Class clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // 这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
            }
        }
        return field;
    }

    /**
     * 利用反射设置指定对象的指定属性为指定的值
     */
    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) {
        final Field field = ReflectTools.getField(obj, fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(obj, fieldValue);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public static boolean hasField(Object obj, String fieldName){
        return ReflectTools.getField(obj, fieldName) != null;
    }
    
    /**
     * 该方法是用于相同对象不同属性值的合并，如果两个相同对象中同一属性都有值，
     * @param sourceBean
     * @param targetBean
     * @return
     * @create_time 2018年9月1日 下午3:43:04
     */
    public static Object combineFieldsCore(Object sourceBean, Object targetBean) {
        Class<?> sourceBeanClass = sourceBean.getClass();
        Class<?> targetBeanClass = targetBean.getClass();
        if(sourceBean == null || targetBean == null){
            return targetBean;
        }
        if(targetBeanClass.isPrimitive() || targetBeanClass.isArray() || targetBean instanceof Collection || targetBean instanceof List 
                || targetBean instanceof Set || targetBean instanceof Map || targetBeanClass.isEnum() || targetBeanClass.isAnnotation()){
            return targetBean;
        }
        if(targetBean instanceof String || targetBean instanceof Byte || targetBean instanceof Short || targetBean instanceof Integer 
                || targetBean instanceof Long || targetBean instanceof Float || targetBean instanceof Double || targetBean instanceof Character 
                || targetBean instanceof Boolean || targetBean instanceof Date || targetBean instanceof BigDecimal){
            return targetBean;
        }
 
        Field[] sourceFields = sourceBeanClass.getDeclaredFields();
        Field[] targetFields = sourceBeanClass.getDeclaredFields();
        for (int i = 0; i < sourceFields.length; i++) {
            Field sourceField = sourceFields[i];
            Field targetField = targetFields[i];
            targetField.setAccessible(true);
            if(java.lang.reflect.Modifier.isFinal(targetField.getModifiers())){
                continue;
            }
            sourceField.setAccessible(true);
            try {
                if (targetField.get(targetBean) == null) {
                    targetField.set(targetBean, sourceField.get(sourceBean));
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return targetBean;
    }

}
