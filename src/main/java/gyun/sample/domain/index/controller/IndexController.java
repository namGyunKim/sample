package gyun.sample.domain.index.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import gyun.sample.global.region.enums.RegionsType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class IndexController {



    @ModelAttribute("regionTypeList")
    public List<String> globalRegionsTypeList(){
         return Arrays.stream(RegionsType.values()).map(RegionsType::getValue).toList();
    }
    @GetMapping(value = "/")
    public String index(HttpServletRequest request){
        return "index";
    }
}
