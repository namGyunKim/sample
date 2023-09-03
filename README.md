# sample
리그오브레전드 전적검색 사이트를 만들어볼 예정

RestApi 프로젝트

화면은 리액트로 구성

올바르지 않은 RequestData를 보내면 400에러를 반환하도록 구현
BindingResult가 메소드에 포함되어 있다면 AOP를 적용한 BindingAdvice를 통해 에러를 처리하도록 구현

<img width="584" alt="image" src="https://github.com/namGyunKim/sample/assets/30253535/4794011f-df91-4c4d-8d64-61a3c80f09f2">


예외처리는 @RestControllerAdvice 및 @ExceptionHandler를 사용하여 구현

<img width="384" alt="image" src="https://github.com/namGyunKim/sample/assets/30253535/2e1ec6a8-b35e-4c64-894f-5f70a6cea67a">

예외처리시 @EventListener를 사용하여 로그를 남기도록 구현

<img width="385" alt="image" src="https://github.com/namGyunKim/sample/assets/30253535/16ff7c4b-f9dc-42dd-9e30-0583740f1dbc">