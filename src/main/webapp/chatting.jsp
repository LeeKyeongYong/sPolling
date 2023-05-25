<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
	 
	<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<style>
body { font-size: 12px; }
#info { color: blue; }
#messages { border: 1px solid black; width: 600px; height: 200px; overflow: scroll; }
</style>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
let t;
$(() => {
	// 페이지가 최초 로딩 될 때, 메시지를 받아온다.
	getMessages();
	$("#myMessage").focus();
	
	$("#sendMessage").on("click", () => {
		if($("#myMessage").val().length === 0) return;
		
		// 메시지를 전송한 다음, 다시 getMessages 함수를 호출 할 것이므로,
		// 현재, 3초후에 예정된 getMessages 함수 호출 예약을 취소
		clearTimeout(t);
		
		$.ajax({
			url: "sendMessage.do",
			type: "post",
			dataType: "text",
			data: { message: $("#myMessage").val() },
			success: (data) => {
				if(data === "SUCCESS") {
					getMessages();
					$("#myMessage").val("");
				} else {
					$("#info").text("세션이 만료 되었습니다.");
				}
			},
			error: (jqXHR, textStatus, errorThrown) => {
				$("#info").text("에러 : " + textStatus);
			}
			});
	});
	
	$("#exit").on("click", () => {
		location.href = "exit.do";
	});
	
	$("#myMessage").on("keydown", (e) => {
		if(e.keyCode === 13) {
			$("#sendMessage").trigger("click");
		}
	});
});

function getMessages() {
	$.ajax({
		url: "getMessages.do",
		type: "get",
		dataType: "json",
		cache: false,
		success: (data) => {
			if(data.result === "FAILURE") {
				//alert("세션이 만료되었습니다.");
				location.href = "index.do";
			} else {
				$.each(data, (index, value) => {
					$("<span>").text(value).appendTo("#messages");
					$("<br>").appendTo("#messages");
					// 채팅창에 메시지가 가득 찰 때, 채팅창의 최하단으로 스크롤
					$("#messages").scrollTop(200);
				});
			}
		},
		error: (jqXHR, textStatus, errorThrown) => {
			$("#info").text("에러 : " + textStatus);
		}
	});
	// 5초후에 getMessages 함수를 다시 호출한다.
	t = setTimeout(getMessages, 5000);
}
</script>
<title>궁금하면500원</title>
</head>
<body>
<h3>채팅룸</h3>
<div id="info">현재 닉네임은 ${param.nickname}입니다.</div>
<hr />
<div id="messages"></div><br />
<input type="text" id="myMessage" name="myMessage" size="60">&nbsp;
<input type="button" id="sendMessage" value="전송">&nbsp;
<input type="button" id="exit" value="나가기">
<hr />
</body>
</html>