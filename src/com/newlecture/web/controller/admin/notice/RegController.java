package com.newlecture.web.controller.admin.notice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.icu.text.MessagePattern.Part;
import com.newlecture.web.entity.Notice;
import com.newlecture.web.service.NoticeService;

@MultipartConfig(
	fileSizeThreshold=1024*1024,
	maxFileSize=1024*1024*50,
	maxRequestSize=1024*1024*50*5
)
@WebServlet("/admin/board/notice/reg")
public class RegController extends HttpServlet{
	// Get��û == �������� �� �ۼ��� ���� ������ �ִ� �뵵
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//forward
		request.getRequestDispatcher("/WEB-INF/view/admin/board/notice/reg.jsp").forward(request, response);
	}
	// Post��û == �������� ���� ����ϸ� �޾Ƽ� ó���� �뵵
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String isOpen = request.getParameter("open");
		
		Collection<javax.servlet.http.Part> parts = request.getParts();
		StringBuilder builder = new StringBuilder();
		for(javax.servlet.http.Part p : parts) {
			if(!p.getName().equals("file")) continue;
			if(p.getSize() == 0) continue;
			
			javax.servlet.http.Part filePart = p;
			String fileName = filePart.getSubmittedFileName();
			builder.append(fileName);
			builder.append(",");
			
			// ���̳ʸ��� �ޱ� ���ؼ��� ��Ʈ�� ���,
			InputStream fis = filePart.getInputStream();
			
			String realPath = request.getServletContext().getRealPath("/member/upload");
			System.out.println(realPath);
			
			File path = new File(realPath);
			if(!path.exists())
				path.mkdirs();
			
			//File.separator ��� ���й���� ���ڷ� ������.
			String filePath = realPath + File.separator + fileName;
			FileOutputStream fos = new FileOutputStream(filePath);
			
			byte[] buf = new byte[1024];
			int size = 0;
			while((size=fis.read(buf)) != -1)
				fos.write(buf,0,size);
			
			fos.close();
			fis.close();
		}
		
		builder.delete(builder.length()-1, builder.length());
		
		
		boolean pub = false;
		if(isOpen != null)
			pub = true;
		
		Notice notice = new Notice();
		notice.setTitle(title);
		notice.setContent(content);
		notice.setPub(pub);
		notice.setWriterId("newlec");
		notice.setFiles(builder.toString());

		NoticeService service = new NoticeService();
		int result = service.insertNotice(notice);
		
		// ó���� �� - �ѱ� ���ڵ� ó��, open���� ��� ó������
		// ����ó���� �؇J�⶧���� �Է��Ҷ� �ѱ��� �������� �ƴ϶� ����� �� �������̴�.
		// �׷��� ����ϱ����� �ѱ� ���ڵ� ó���� �������.
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		
		response.sendRedirect("list");
	}
}