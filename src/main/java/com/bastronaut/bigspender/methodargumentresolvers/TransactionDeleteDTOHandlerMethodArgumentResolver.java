package com.bastronaut.bigspender.methodargumentresolvers;

import com.bastronaut.bigspender.dto.in.TransactionDeleteDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.bastronaut.bigspender.utils.ApplicationConstants.TRANSACTIONID_PARAM;

/**
 *  Custom injection of method parameter for controller. Customized because a list of
 *  https://www.petrikainulainen.net/programming/spring-framework/spring-from-the-trenches-creating-a-custom-handlermethodargumentresolver/
 *
 */
public class TransactionDeleteDTOHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(TransactionDeleteDTO.class);
    }


    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer, final NativeWebRequest request,
                                  final WebDataBinderFactory binderFactory)
            throws Exception {

        final String transactionIds = request.getParameter(TRANSACTIONID_PARAM);
        final String[] transactionIdsSplit = StringUtils.split(transactionIds, ",");
                transactionIds.split(",");
//        final String transactionIds =
        Map<String, String[]> params = request.getParameterMap();
        Iterator<String> headernames = request.getHeaderNames();
        String cp = request.getContextPath();
        String a = "11";
        List<Long> deleted = new ArrayList<>();
        deleted.add(1L);
        deleted.add(2L);
        return null;
//        return new TransactionDeleteResultDTO(2, deleted);
    }

    private boolean isNotSet(final String value) {
        return value == null;
    }
}
