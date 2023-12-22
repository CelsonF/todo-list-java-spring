package br.com.celsonf.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.celsonf.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import br.com.celsonf.todolist.user.IUserRepository;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            
                var servletPath = request.getServletPath();

                if(servletPath.equals("/tasks/")) {

                    var autorization = request.getHeader("Authorization");

                var authEncoded = autorization.substring("Basic".length()).trim();
            
                byte[] authDecode = Base64.getDecoder().decode(authEncoded);

                var authString = new String(authDecode);

                String[] credenctials = authString.split(":");
                String userName = credenctials[0];
                String password = credenctials[1];

                System.out.println(userName);
                System.out.println(password);
                
                var user = this.userRepository.findByUserName(userName);
                if (user == null) {
                    response.sendError(401);
                } else {



                    var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(),user.getPassword());

                    if(passwordVerify.verified) {
                        filterChain.doFilter(request,response);
                    } else {
                        response.sendError(401);
                    }

                }
                
            } else {
                filterChain.doFilter(request, response);
            }



    }

}