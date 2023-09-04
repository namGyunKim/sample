# sample
리그오브레전드 전적검색 사이트를 만들어볼 예정

RestApi 프로젝트

화면은 리액트로 구성

올바르지 않은 RequestData를 보내면 400에러를 반환하도록 구현
BindingResult가 메소드에 포함되어 있다면 AOP를 적용한 BindingAdvice를 통해 에러를 처리하도록 구현

<img width="671" alt="image" src="https://github.com/namGyunKim/sample/assets/30253535/9ba3e831-c550-4d59-9ee6-4ec2f2633450">

예외처리는 @RestControllerAdvice 및 @ExceptionHandler를 사용하여 구현

<img width="422" alt="image" src="https://github.com/namGyunKim/sample/assets/30253535/35b585cc-fd55-4997-b66c-a3921ae3052d">

예외처리시 @EventListener를 사용하여 로그를 남기도록 구현

![image](https://github.com/namGyunKim/sample/assets/30253535/600da971-420f-4c83-8b62-caeb35407056)
