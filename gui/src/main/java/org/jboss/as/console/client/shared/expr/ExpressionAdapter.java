package org.jboss.as.console.client.shared.expr;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * API to read/write expression overlays on entities.
 *
 * @author Heiko Braun
 * @date 10/17/11
 */
public class ExpressionAdapter {

    private static final String EXPR_TAG = "EXPRESSIONS";

    public static Map<String,String> getExpressions(Object bean)
    {
        final AutoBean autoBean = asAutoBean(bean);

        Map<String, String> exprMap = (Map<String,String>)autoBean.getTag(EXPR_TAG);
        if(null==exprMap)
        {
            exprMap = new HashMap<String,String>();
            autoBean.setTag(EXPR_TAG, exprMap);
        }

        return exprMap;
    }

    private static AutoBean asAutoBean(Object bean) {
        final AutoBean autoBean = AutoBeanUtils.getAutoBean(bean);
        if(null==autoBean)
            throw new IllegalArgumentException("Not an auto bean: " + bean.getClass());
        return autoBean;
    }


    public static void setExpressionValue(Object entity, String javaName, String exprValue) {
        getExpressions(entity).put(javaName, exprValue);
    }

    public static String getExpressionValue(Object entity, String javaName) {
        return getExpressions(entity).get(javaName);
    }
}
