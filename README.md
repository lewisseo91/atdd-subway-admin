<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-%3E%3D%205.5.0-blue">
  <img alt="node" src="https://img.shields.io/badge/node-%3E%3D%209.3.0-blue">
  <a href="https://edu.nextstep.camp/c/R89PYi5H" alt="nextstep atdd">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
  <img alt="GitHub" src="https://img.shields.io/github/license/next-step/atdd-subway-admin">
</p>

<br>

# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

<br>

## 🚀 Getting Started

### Install
#### npm 설치
```
cd frontend
npm install
```
> `frontend` 디렉토리에서 수행해야 합니다.

### Usage
#### webpack server 구동
```
npm run dev
```
#### application 구동
```
./gradlew bootRun
```
<br>

## ✏️ Code Review Process
[텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

<br>

## 🐞 Bug Report

버그를 발견한다면, [Issues](https://github.com/next-step/atdd-subway-admin/issues) 에 등록해주세요 :)

<br>

## 📝 License

This project is [MIT](https://github.com/next-step/atdd-subway-admin/blob/master/LICENSE.md) licensed.

## 요구 사항 기능

- [x] 노선 생성 시 종점역(상행, 하행) 정보를 요청 파라미터에 함께 추가하기 
  - 두 종점역은 구간의 형태로 관리되어야 함
  - 구간 중에 downStation 쪽에 distance를 저장한다. (후방 노드에 distance가 remove에 작업이 좀 더 쉬울 것이라 예측)
  - 종점역 정보는 선택으로 받는다. (역 정보가 들어올 수도, 노선만 추가될 수도 있다고 생각)
- [x] 노선 조회 시 응답 결과에 역 목록 추가하기 
  - 상행역 부터 하행역 순으로 정렬되어야 함
  
### 구간 추가 기능

#### 일반 기능

- [x] 새로운 역을 상행 종점으로 등록한다.
  - 조건 : 기존의 first downStation == 요청 downStation
  - 새로운 상행 종점역이 추가되고 기존 상행 종점역에 길이가 추가된다.
- [x] 새로운 역을 하행 종점으로 등록할 경우
  - 조건 : 기존의 last downStation == 요청 upStation
  - 새로운 구간이 추가되고 기존 유지, 새로운 역을 등록한다.
- [x] 역 사이에 새로운 역을 등록한다.
  - 조건 : 위의 두 조건이 아닐 경우 (그러나 downStation 중에 하나가 걸쳐있다.) && 예외에 속하지 않는 경우
  - 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정한다.
  
#### 예외 기능 

- [x] 역 사이 요청 역의 길이가 기존 역 길이보다 긴 경우
- [x] 기존에 역이 등록 되어 있는 경우
- [x] 추가할 역이 모두 등록 되어 있지 않은 경우

#### 고려한 점

- 구간은 ArrayList로 정한다.
  - LinkedList를 제외한 이유 : 지하철 역 특성상 add나 remove 되는 숫자는 적을 것이고 조회 횟수는 많을 것이라 생각하였기 때문
  - hashMap을 제외한 이유 : LinkedHashMap을 이용해 공간 복잡도를 조금 더 소모하고 시간 복잡도를 줄이는 의도를 생각하였지만 종점 바뀔 시에 컨트롤하는 비용이 클 것 같아 제외

### 노선 구간 제거 기능

#### 일반 기능

- [x] 상행 종점 역을 제거한다.
- [x] 중간 역을 제거한다.
- [x] 하행 종점 역을 제거한다.
  
#### 예외 기능

- [x] 노선에 등록되어있지 않은 역을 제거하려 한다.
- [x] 구간이 하나인 노선에서 마지막 구간을 제거하려 한다.