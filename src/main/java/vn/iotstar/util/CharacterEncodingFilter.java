package vn.iotstar.util;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * Filter thiết lập encoding UTF-8 và điều phối RequestDispatcher tương thích SiteMesh 3 trên Tomcat 11
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
        if (response.getContentType() == null) {
            response.setContentType("text/html;charset=" + encoding);
        }

        if (request instanceof HttpServletRequest httpRequest) {
            HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public RequestDispatcher getRequestDispatcher(final String path) {
                    final RequestDispatcher dispatcher = super.getRequestDispatcher(path);
                    if (dispatcher == null) {
                        return null;
                    }
                    return new RequestDispatcher() {
                        @Override
                        public void forward(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
                            // Trên Tomcat 11, forward() commit response facade dẫn đến SiteMesh 3
                            // bỏ qua postProcess vì response.isCommitted() == true.
                            // Chuyển sang include() để bảo đảm buffer SiteMesh thu thập trọn vẹn và không bị commit sớm.
                            dispatcher.include(req, resp);
                        }

                        @Override
                        public void include(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
                            dispatcher.include(req, resp);
                        }
                    };
                }
            };
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {}
}
