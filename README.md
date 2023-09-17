# sample
리그오브레전드 전적검색 사이트를 만들어볼 예정

RestApi 프로젝트

화면은 리액트로 구성

올바르지 않은 RequestData를 보내면 400에러를 반환하도록 구현
BindingResult가 메소드에 포함되어 있다면 AOP를 적용한 BindingAdvice를 통해 에러를 처리하도록 구현

https://blog.naver.com/skarbs01/223210410721

![img.png](gitimage/img.png)

![img_1.png](gitimage/img_1.png)

예외처리는 @RestControllerAdvice 및 @ExceptionHandler를 사용하여 구현

![img_2.png](gitimage/img_2.png)

예외처리시 @EventListener를 사용하여 로그를 남기도록 구현

![img_3.png](gitimage/img_3.png)

로그인 기능은 JWT 로 구현 RefreshToken은 Redis에 저장

https://blog.naver.com/skarbs01/223210430457

카카오 로그인의 경우 OpenFeign을 이용하여 카카오 api를 사용하여 구현하였으며

getCode getTokenByCode getInfoByToken 의 3가지 api를 사용하였습니다.

stomp 와 redis 를 사용한 채팅기능 구현


라이엇 api 는 공식문서를 보고 구현하였습니다.
https://developer.riotgames.com/apis#summoner-v4/GET_getBySummonerName
