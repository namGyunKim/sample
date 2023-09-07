package gyun.sample.domain.index.controller;

import gyun.sample.global.region.enums.RegionsType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class IndexController {



    //    전역으로 사용할 수 있는 지역 리스트
    @ModelAttribute("regionTypeList")
    public List<String> globalRegionsTypeList(){
         return Arrays.stream(RegionsType.values()).map(RegionsType::getValue).toList();
    }

    //    메인 페이지
    @GetMapping(value = "/")
    public String index(HttpServletRequest request){
        return "index";
    }
}
