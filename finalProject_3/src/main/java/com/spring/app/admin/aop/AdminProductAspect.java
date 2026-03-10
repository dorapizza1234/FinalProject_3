package com.spring.app.admin.aop;

import java.util.List;
import java.util.Locale;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import com.spring.app.product.domain.ProductDTO;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class AdminProductAspect {

    // 관리자 전용 금지어 목록 (소라님이 주신 리스트 그대로 활용)
    private static final List<String> FORBIDDEN_WORDS = List.of(
        // 욕설 / 비속어
        "씨발","시발","ㅅㅂ","ㅆㅂ","개새끼","개새","개자식","병신","븅신","ㅂㅅ","지랄","염병","미친놈","미친년","미친새끼","또라이","또라이새끼","좆","존나","좆같","ㅈㄴ","꺼져","닥쳐","죽어","씹","씹새","씹새끼","걸레","창녀","창년",
        // 개인정보
        "주민등록번호","주민번호","민증","신분증","여권","운전면허증","면허증","등본","초본","인감","인감증명서","통장사본","유심","USIM","번호판매","회선판매","명의",
        // 금융/환전
        "통장판매","계좌판매","체크카드","신용카드","카드깡","현금화","대출","암호화폐","코인","비트코인","USDT","환전","환치기","외화","송금대행",
        // 해외/면세
        "해외직구","직구","구매대행","배송대행","면세","면세품","면세점","duty free","tax free",
        // 가품/침해
        "레플리카","레플","짝퉁","이미테이션","가품","정품아님","미러급","SA급","불법복제","복제본",
        // 동물 / 건강 / 담배 / 티켓 등 나머지 생략 (소라님 리스트 전부 포함시키시면 됩니다)
        "강아지분양","고양이분양","분양","교배","건강기능식품","건기식","홍삼","의약품","처방약","담배","전자담배","주류","술","맥주","소주","샘플","증정","비매품","상품권","기프티콘","티켓","양도","게임머니","아이템판매","식품","먹거리","도난","장물","몰카","도청","불법","마약","대마"
    );

  
    // 예: AdminService의 getProductList 메서드가 실행될 때
    @Pointcut("execution(* com.spring.app.admin.service.AdminService.getProductList(..))")
    public void adminProductListMethods() {}

    // [AfterReturning] 조회가 완료되어 리스트를 반환한 직후에 실행
    @AfterReturning(pointcut = "adminProductListMethods()", returning = "result")
    public void checkSuspectAfterReturning(Object result) {
        if (result instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof ProductDTO dto) {
                    // 제목과 설명 중 하나라도 걸리면 suspect = true
                    boolean isSuspect = hasForbiddenWord(dto.getProductName()) || hasForbiddenWord(dto.getProductDesc());
                    dto.setSuspect(isSuspect);
                    
                    if(isSuspect) {
                        log.info(">>> [관리자 모니터링] 의심 상품 발견: {}", dto.getProductName());
                    }
                }
            }
        }
    }

    // 금지어 포함 여부 확인 로직
    private boolean hasForbiddenWord(String text) {
        if (text == null || text.isBlank()) return false;

        // "씨1발" -> "씨발"로 만드는 정규화 처리
        String normalizedText = normalizeForAdmin(text);

        for (String badWord : FORBIDDEN_WORDS) {
            // 금지어 자체도 공백이나 특수문자 제거 후 비교
            String cleanBadWord = normalizeForAdmin(badWord);
            if (normalizedText.contains(cleanBadWord)) {
                return true;
            }
        }
        return false;
    }

    //  관리자용 정규화 로직
    private String normalizeForAdmin(String str) {
        if (str == null) return "";
        
        return str
                .replaceAll("[^ㄱ-ㅎ가-힣a-zA-Z]", "") // 한글, 영문만 남기고 숫자/특수문자/공백 싹 제거
                .toLowerCase(Locale.ROOT);           // 소문자로 통일
    }
}
