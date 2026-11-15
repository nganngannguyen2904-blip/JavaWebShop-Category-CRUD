package vn.iotstar.util;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Filter thiết lập encoding UTF-8 cho toàn bộ request/response
 */
@WebFilter(urlPatterns = "/*")
public class CharacterEncodingFilter implements Filter {

    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig config) throws ServletException {
        String enc = config.getInitParameter("encoding");
        if (enc != null && !enc.isEmpty()) {
            this.encoding = enc;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
