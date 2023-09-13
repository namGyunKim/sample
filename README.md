# sample
리그오브레전드 전적검색 사이트를 만들어볼 예정

RestApi 프로젝트

화면은 리액트로 구성

올바르지 않은 RequestData를 보내면 400에러를 반환하도록 구현
BindingResult가 메소드에 포함되어 있다면 AOP를 적용한 BindingAdvice를 통해 에러를 처리하도록 구현

https://blog.naver.com/skarbs01/223210410721

![image](https://github.com/namGyunKim/sample/assets/30253535/bf83dd2e-bec8-476a-80bc-a52590eb8f3b)

예외처리는 @RestControllerAdvice 및 @ExceptionHandler를 사용하여 구현

<img width="422" alt="image" src="https://github.com/namGyunKim/sample/assets/30253535/35b585cc-fd55-4997-b66c-a3921ae3052d">

예외처리시 @EventListener를 사용하여 로그를 남기도록 구현

<img width="384" alt="image" src="https://github.com/namGyunKim/sample/assets/30253535/a6871642-bfd7-4f7e-b31c-89a6095d0305">



로그인 기능은 JWT 로 구현 RefreshToken은 Redis에 저장

https://blog.naver.com/skarbs01/223210430457

stomp 와 redis 를 사용한 채팅기능 구현

OpenFeign을 사용하여 외부 api를 호출

JWT 를 사용한 로그인 기능 구현

로그인의 경우 JWT를 사용하여 로그인을 구현하였고 카카오 로그인의 경우 카카오 api를 사용하여 구현하였습니다.

ResreshToken의 경우 Redis에 저장하여 구현하였습니다.

Kakao의 경우 getCode getTokenByCode getInfoByToken 의 3가지 api를 사용하였습니다.

라이엇 api 는 공식문서를 보고 구현하였습니다.
https://developer.riotgames.com/apis#summoner-v4/GET_getBySummonerName
