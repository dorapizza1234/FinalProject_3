package com.spring.app.auth.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.spring.app.member.domain.MemberDTO;

import lombok.Getter;


@Getter
public class CustomUserDetails implements UserDetails {
	// Spring Security 에서 인증된 사용자의 정보는 UserDetails 인터페이스를 구현하여 관리한다.
    // UserDetails 인터페이스를 구현한 클래스는 로그인한 사용자의 정보 및 사용자의 권한(Role), 인증 토큰(JWT) 등을 포함할 수 있는 객체가 되어진다.

	private static final long serialVersionUID = 1L;

	private MemberDTO memberDto;

	public CustomUserDetails(MemberDTO memberDto) {
		this.memberDto = memberDto;
	}


	// 권한종류
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return memberDto.getAuthorities().stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .toList(); // JDK 16 이상
	}


	// 아이디
	@Override
	public String getUsername() {
		return memberDto.getMemberid();
	}


	// 비밀번호
	@Override
	public String getPassword() {
		return memberDto.getPasswd();
	}


	@Override
    public boolean isAccountNonExpired() {
		return true;
    }

    @Override
    public boolean isAccountNonLocked() {
    	return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
    	return true;
    }

    // *** 추가 정보 접근용 getter *** //
    // 회원명
    public String getMemberName() {
    	return memberDto.getName();
    }


    // 가입일자
    public LocalDate getRegisterday() {
    	return memberDto.getRegisterday();
    }


    // 최근에 마지막으로 로그인한 일자 및 시각
    public String getLastLoginDate() {
    	return String.join(" ", String.valueOf(memberDto.getLastLoginDate()).split("T"));
    }

}
