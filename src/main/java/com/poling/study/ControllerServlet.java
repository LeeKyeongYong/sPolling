package com.poling.study;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;


import com.google.gson.Gson;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class ControllerServlet
 */
public class ControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Messages messages = Messages.getInstance();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ControllerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String contextPath = this.getServletContext().getContextPath();
		String url = request.getRequestURI().substring(contextPath.length());

		if(url.equals("/index.do")) {
			String error = request.getParameter("error");
			
			// 이미 존재하는 닉네임으로 접속한 경우 에러 메시지 표시
			if(error != null && error.equals("nickname")) {
				request.setAttribute("error", "이미 존재하는 닉네임입니다. 다른 닉네임을 입력해주세요.");
			}
			
			// 세션이 만료된 경우 에러 메시지 표시
			if(error != null && error.equals("session")) {
				request.setAttribute("error", "세션이 만료되어 채팅방으로 입장할 수 없습니다.");
			}
			
			// 첫 페이지 표시
			RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
			dispatcher.forward(request, response);
			
		} else if(url.equals("/getMessages.do")) {
			// 세션에서 닉네임 가져오기
			HttpSession session = request.getSession();
			String nickname = (String)session.getAttribute("nickname");
			String json = null;
			
			System.out.println(nickname + "님의 메시지 요청: " + new Date());
			
			if(nickname == null) {
				// 세션에 닉네임이 없는 경우
				json = "{ \"result\" : \"FAILURE\" }";
				
			} else {
				Gson gson = new Gson();
				
				synchronized(messages) {
					List<String> myMessages = messages.getMyMessages(nickname);
					// JSON 형식으로 변환하여 전송
					json = gson.toJson(myMessages);
					messages.removeMyMessage(nickname);
				}
			}
			
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			PrintWriter writer = response.getWriter();
			writer.write(json);
			writer.flush();
			writer.close();
			
		} else if(url.equals("/exit.do")) {
			HttpSession session = request.getSession();
			String nickname = (String)session.getAttribute("nickname");

			if(nickname != null) {
				session.invalidate();
			}
			response.sendRedirect("index.do");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String contextPath = this.getServletContext().getContextPath();
		String url = request.getRequestURI().substring(contextPath.length());
		request.setCharacterEncoding("UTF-8");
		
		if(url.equals("/enterRoom.do")) {
			// 이미 존재하는 닉네임인지 확인 후 처리
			String nickname = request.getParameter("nickname");
			
			if(messages.existNickname(nickname)) {
				response.sendRedirect("index.do?error=nickname");
				
			} else if(request.getSession().getAttribute("nickname") != null) {
				response.sendRedirect("index.do?error=session");
				
			} else {
				// 새로운 닉네임으로 세션 생성
				HttpSession session = request.getSession();
				session.setAttribute("nickname", nickname);
				session.setMaxInactiveInterval(10); // 10초 동안 요청이 없으면 세션 만료
			
				// 닉네임 목록에 추가
				messages.addNickname(nickname);

				// 채팅방으로 이동
				RequestDispatcher dispatcher = request.getRequestDispatcher("chatting.jsp");
				dispatcher.forward(request, response);
			}
			
		} else if(url.equals("/sendMessage.do")) {

			String result = null;
			HttpSession session = request.getSession();
			String nickname = (String)session.getAttribute("nickname");
			
			if(nickname == null) {
				// 세션이 만료되었다면 실패 처리
				result = "FAILURE";
			} else {
				// 전송된 메시지를 모든 사용자에게 전달
				String message = request.getParameter("message");
				messages.addMessageToAll("[" + nickname + "] " + message);
			
				// 성공 처리
				result = "SUCCESS";
			}
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/plain");
			PrintWriter writer = response.getWriter();
			writer.write(result);
			writer.flush();
			writer.close();
		}
	}
}
