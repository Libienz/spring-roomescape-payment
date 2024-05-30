package roomescape.login;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.auth.Authenticated;
import roomescape.global.util.CookieUtils;
import roomescape.login.dto.Accessor;
import roomescape.login.infrastructure.JwtTokenProvider;
import roomescape.member.service.MemberService;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    public LoginMemberArgumentResolver(JwtTokenProvider jwtTokenProvider, MemberService memberService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberService = memberService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Authenticated.class) &&
                parameter.getParameterType().equals(Accessor.class);
    }

    @Override
    public Accessor resolveArgument(MethodParameter parameter,
                                    ModelAndViewContainer mavContainer,
                                    NativeWebRequest webRequest,
                                    WebDataBinderFactory binderFactory) {
        String accessToken = CookieUtils.getToken((HttpServletRequest) webRequest.getNativeRequest());
        Long id = jwtTokenProvider.getAccessorId(accessToken);
        return new Accessor(id);
    }
}