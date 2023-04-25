# foodiary_project_back (푸디어리 프로젝트 백엔드)

### **✅한줄 소개** 
오늘 뭐 먹을까를 대신 고민해서 해결해주고, 자신이 먹은 메뉴를 공유할 수 있는 서비스

### 🔗Link

[푸디어리 사이트](https://foodiary.netlify.app/)

### **📖 상세 내용**

> 💡 온라인으로 인원 모집부터 배포까지 진행한 프로젝트입니다. 날이면 날마다 고민하는 오늘 뭐 먹을까?를 항상 고민하는 사람들을 위해 대신 고민해주고 또 해결해 줄 서비스로 매번 색다른 메뉴를 추천합니다. 또한 자신이 오늘 무엇을 먹었는지 기록하고 또 공유할 수 있는 서비스입니다.

<img width=50% src="https://user-images.githubusercontent.com/112879800/234303756-c8f87b18-ae1a-481f-9cc2-5f78e7e3d956.png">
<img width=44% src="https://user-images.githubusercontent.com/112879800/234303895-e3c4c65e-a7be-44d5-9619-7283b8ba8f0d.png">

<br>

### 🛠️ 사용 기술 및 라이브러리

- java
- Spring Boot, Spring MVC
- Mybatis
- Gradle
- AWS EC2, S3, RDS, Route 53, Elastic Beanstalk
- MySql, MariaDB, redis
- Github Action

### 🙋🏻‍♀️ 담당한 역할

팀 리더, 인프라 설계 및 회원 서비스 및 랭킹 서비스 구축

### 📱 구현 기능 (API)

- Elastic Beanstalk 및 Route 53, ACM(AWS Certificate Manager)을 활용하여 SSL 환경 구성을 통해 통신 보안 구축
- github Action을 사용하여 CI/CD 구축을 통해 배포 시 빌드 테스트 및 배포 자동화 구축
- Sendgrid를 활용한 이메일 api 연동, 템플릿 제작 및 관리, 이메일 발송 서비스 구현을 통해 회원가입 시 이메일 인증으로 본인 확인
    
    Sendgrid의 템플릿 관리 서비스를 이용하여, 이메일 발송할 타입에 맞춘 템플릿을 적용하여 이메일 발송
    
   <img width="838" alt="2" src="https://user-images.githubusercontent.com/112879800/234305524-17a60e29-ca73-4616-8695-82d478b44abe.png">

    

- @Scheduled의 cron식을 활용하여 랭킹 시스템 구현
    
    1시간 마다 랭킹 업데이트 작업을 실행
    
    <img width="838" alt="2" src="https://user-images.githubusercontent.com/112879800/234305918-df6897f9-7a4d-43bb-8b86-36494c065cd4.png">

    
- redis를 통한 최근 검색어 저장 및 관리
    
    키값을 “dailySearch:+회원시퀀스”로 하여 회원들의 최근 검색어를 저장
    
    최근 검색어는 최대 10개까지 저장하므로, 10개를 초과할 경우 가장 먼저 들어온 최근 검색어를 삭제
    
    <img width="838" alt="2" src="https://user-images.githubusercontent.com/112879800/234306426-12c73b3f-7bd4-4701-aa2f-4bc870892c43.png">

    
    이 외에도 TTL 기능을 활용하여 회원 인증코드 관리, 유효시간이 지난 후 자동으로 데이터 삭제
    

- Transactional 처리를 통해서 에러 발생시 데이터 손실 방지
    
    rollbackFor 옵션을 통해 특정 예외가 발생 시 강제로 RollBack 처리
    
    <img width="838" alt="2" src="https://user-images.githubusercontent.com/112879800/234306730-25d6b9f6-5c51-4f9f-ae79-a65a8f7f8741.png">


    

### 💡 성과

- Elastic Beanstalk의 어플리케이션 버전 관리 기능
    - 해당 기능이 있어 코드를 롤백하지 않더라도 이전 버전의 소스코드를 쉽게 배포
- SSL 환경 구성을 통해 통신 보안 구축
- AWS EB를 활용하여 배포하여 배포 버전 관리 방법 터득
    - 롤백을 하지 않더라도, 이전 소스 코드로 배포를 쉽게 배포
- SendGrid api를 사용하여 메일 발송 및 관리
    - 메일 전송과 관련된 통계 및 메일 수신 여부 확인
- redis를 활용하여 유효기간 부여를 통해 데이터 자동 삭제를 통한 데이터 관리의 효율성 증대
- 스웨거를 통해 api문서 자동화 및 사용법 터득
    - 조회수나 좋아요 카운트를 update문을 통해 진행하였는데, redis를 통해 구현하여 RDB 부하 감소