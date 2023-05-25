<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	<!DOCTYPE html>
	<html>
	<head>
	<meta charset="UTF-8">
	<style>
	body {
	  font-size: 12px;
	}
	
	.error {
	  color: red;
	}
	</style>
	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
	<script>
	const check = () => {
	  if ($("#nickname").val().length === 0) {
		alert("닉네임을 지정해주세요.");
		$("#nickname").focus();
		return false;
	  }
	  return true;
	};
	</script>
	<title>롱폴링 샘플코드</title>
	</head>
	<body>
	<h3>Polling 방식의 초간단 채팅</h3>
	- 코드는 <a href="https://blog.naver.com/sleekydz86">블로그 궁금하면오백원</a>에서 제공합니다.<br /><br />
	- 세션 기반으로 닉네임을 구분하기 때문에 동일한 브라우저들로 채팅방에 동시에 입장 할 수 없습니다.<br /><br />
	- 테스트하시려면 서로 다른 브라우저로 접속하세요. 예) 사파리와 크롬등...<br /><br />
	- Polling 방식을 사용하기 때문에 상대방의 메시지를 확인하기 까지 최대 5초가 소요될 수 있습니다.<br /><br />
	<div class="error">${error}</div>
	<hr />
	<form action="enterRoom.do" method="post" onsubmit="return check();">
	  <label for="nickname">닉네임 : </label>&nbsp;
	  <input type="text" name="nickname" id="nickname" size="20">&nbsp;
	  <input type="submit" value="입장">
	</form>
	<hr />
	</body>
	</html>
	