package gyun.sample.domain.index.controller;

import gyun.sample.global.region.enums.RegionsType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class IndexController {

    @Value("${social.kakao.clientId}")
    private String clientId;
    @Value("${social.kakao.redirectUri}")
    private String redirectUri;


    //    전역으로 사용할 수 있는 지역 리스트
    @ModelAttribute("regionTypeList")
    public List<String> globalRegionsTypeList(){
         return Arrays.stream(RegionsType.values()).map(RegionsType::getValue).toList();
    }

    //    메인 페이지
    @GetMapping(value = "/")
    public String index(Model model){
        model.addAttribute("clientId", clientId);
        model.addAttribute("redirectUri", redirectUri);
        return "index";
    }




}
