package com.example.toyprojectback.controller;


import com.example.toyprojectback.dto.BoardCreateRequest;
import com.example.toyprojectback.dto.BoardDto;
import com.example.toyprojectback.dto.BoardSearchRequest;
import com.example.toyprojectback.dto.CommentCreateRequest;
import com.example.toyprojectback.entity.BoardCategory;
import com.example.toyprojectback.service.BoardService;
import com.example.toyprojectback.service.UploadImageService;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
@Tag(name = "Board", description = "게시판 관련 API")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentServie;
    private final UploadImageService uploadImageService;
    private final LikeService likeService;

    @GetMapping("/{category}")
    public String boardListPage(@PathVariable String category, Model model,
                                @RequestParam(required = false, defaultValue = "1") int page,
                                @RequestParam(required = false) String sortType,
                                @RequestParam(required = false) String searchType,
                                @RequestParam(required = false) String keyword) {


        BoardCategory boardCategory = BoardCategory.of(category);
        if (boardCategory == null) {
            model.addAttribute("message", "존재하지 않는 게시판 입니다.");
            model.addAttribute("nextUrl", "/");
            return "printMessage";
        }

        model.addAttribute("notices", boardService.getNotice(boardCategory));
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("id").descending());
        if (sortType != null) {
            if (sortType.equals("date")) {
                pageRequest = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());
            } else if (sortType.equals("like")) {
                pageRequest = PageRequest.of(page - 1, 10, Sort.by("likeCnt").descending());

            } else if (sortType.equals("comment")) {
                pageRequest = PageRequest.of(page - 1, 10, Sort.by("commentCnt").descending());
            }
        }
        model.addAttribute("category", category);
        model.addAttribute("boards", boardService.getBoardList(boardCategory, pageRequest, searchType, keyword));
        model.addAttribute("boardSearchRequest", new BoardSearchRequest(sortType, searchType, keyword));
        return "boards/list";
    }

    @GetMapping("/{category}/write")
    public String boardWritePage(@PathVariable String category, Model model) {
        BoardCategory boardCategory = BoardCategory.of(category);
        if (boardCategory == null) {
            model.addAttribute("message", "카테고리가 존재하지 않습니다");
            model.addAttribute("nextUrl", "/");
            return "printMessage";
        }
        model.addAttribute("category", category);
        model.addAttribute("boardCreateRequest", new BoardCreateRequest());
        return "board/write";
    }

    @PostMapping("/{category}")
    public String boardWrite(@PathVariable String category, @ModelAttribute BoardCreateRequest req,
                             Authentication auth, Model model) throws IOException {
        BoardCategory boardCategory = BoardCategory.of(category);
        if (boardCategory == null) {
            model.addAttribute("message", "카테고리가 존재하지 않습니다");
            model.addAttribute("nextUrl", "/");
            return "printMessage";

        }
        Long saveBoardId = boardService.writeBoard(req, boardCategory, auth.getName(), auth);
        if (boardCategory.equals(BoardCategory.GREETING)) {
            model.addAttribute("message", "SILVER 등급으로 승급 하였습니다! \n이제 자유게시판에 글을 작성할 수 있습니다.");
        } else {
            model.addAttribute("message", saveBoardId + "번 글이 등록되었습니다.");
        }
        model.addAttribute("message", saveBoardId + "번 글이 등록되었습니다.");
        return "printMessage";
    }

    @GetMapping("/{category}/{boardId}")
    public String boardDetailPage(@PathVariable String category, @PathVariable Long boardId, Model model, Authentication auth) {
        if (auth != null) {

            model.addAttribute("loginUserLoginId", auth.getName());
            model.addAttribute("likeCheck", likeService.checkLike(auth.getName(), boardId));
        }

        BoardDto boardDto = boardService.getBoard(boardId, category);

        if (boardDto == null) {
            model.addAttribute("message", "해당 게시글이 존재하지 않습니다");
            model.addAttribute("nextUrl", "/boards/" + category);
            return "printMessage";
        }
        model.addAttribute("boardDto", boardDto);
        model.addAttribute("category", category);

        model.addAttribute("commentCreateRequest", new CommentCreateRequest());
        model.addAttribute("commentList", commentServie.findAll(boardId));

        return "boards/detail";
    }

    @GetMapping("/{category}/{boardId}")
    public String boardDetailPage(@PathVariable String category, @PathVariable Long boardId, Model model, Authentication auth) {

        if (auth != null) {
            model.addAttribute("loginUserLoginId", auth.getName());
            model.addAttribute("likeCheck", likeService.checkLike(auth.getName(), boardId));

        }
        BoardDto boardDto = boardService.getBoard(boardId, category);
        //id에 해당하는 게시글이 없거나 카테고리가 일치하지 않는 경우

        if (boardDto == null) {
            model.addAttribute("message", "해당 게시글이 존재하지 않습니다");
            model.addAttribute("nextUrl", "/boards/" + category);

            return "printMessage";
        }
        model.addAttribute("boardDto", boardDto);
        model.addAttribute("category", category);

        model.addAttribute("commentCreateRequest", new CommentCreateRequest());
        model.addAttribute("commentList", commentServie.findAll(boardId));

        return "boards/detail";

    }

    @PostMapping("/{category}/{boardId}/edit")
    public String boardEdit(@PathVariable String category, @PathVariable Long boardId, @ModelAttribute BoardDto dto, Model model) throws IOException {
        Long editBoardId = boardService.editBoard(boardId, category, dto);

        if (editBoardId == null) {
            model.addAttribute("message", "해당 게시글이 존재하지 않습니다.");
            model.addAttribute("nextUrl", "/boards/" + category);

        } else {

            model.addAttribute("message", editBoardId + "번 글이 수정되었습니다.");
            model.addAttribute("nextUrl", "/boards/" + category + "/" + editBoardId);
        }
        return "printMessage";
    }

    @GetMapping("/{category}/{boardId}/delete")
    public String boardDelete(@PathVariable String category, @PathVariable Long boardId, Model model) {
        if (category.equals("greeting")) {
            model.addAttribute("message", "가입인사는 삭제할 수 없습니다");
            model.addAttribute("nextUrl", "/boards/greeting");
            return "printMessage";
        }
        Long deleteBoardId = boardService.deleteBoard(boardId, category);

        //id에 해당하는 게시글이 없거나 카테고리가 일치하지 않으면 에러 메세지 출력
        //게시글이 존재해 삭제 했으면 삭제 완료 메세지 출력

        model.addAttribute("message",deleteBoardId==null ? "해당 게시글이 존재하지 않습니다" : deleteBoardId + "번 글이 삭제되었습니다.");
        model.addAttribute("nextUrl", "/boards/" + category);

        return "printMessage";
    }

    @ResponseBody
    @GetMapping("/images/{filename")
    public Resource showImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:"+uploadImageService.getFullPath(filename));

    }

    @GetMapping("/images/download/{boardId}")
    public ResponseEntity<UrlResource> downloadImage(@PathVariable Long boardId) throws MalformedURLException {
        return uploadImageService.downloadImage(boardId);
    }

}

/*
* /board/{category} : 카테고리에 해당하는 글 리스트를 return 하는 URL
*   sortType으로 정렬 방식을 입력받아 PageRequest 설정을 통해 정렬 구현
*   searchType 과 keyword로 글 제목,작성자 닉네임에 keyword가 포함되는 글을 return함으로서 검색 기능 구현
* /boards/{category}/{boardId} 게시글 상제 조회 페이지
*   만약 로그인 한 유저라면 loginUserLoginId를 전송함으로서, 화면에서 본인이 작성한 글인지 확인할 수 있게 함
*   로그인 한 유저가 이 글에 좋아요를 눌렀는지 여부를 likeCheck에 담아 전송함으로서, 화면에서 하트를 누르면 좋아요를 추가 시킬지, 제거 시킬지 알수 있게함
*   또한 글 상세페이지에서는 해당 글에 추가된 댓글리스트도 같이 전송 시킴
*
* /boards/images/{filename} : Resource 타입으로 파일을 전송함으로, 해당 게시글에 추가된 이미지를 화면에서 출력시킴
* /boards/images/download/{filename}: ResponseEntity<UrlResource> 타입으로 파일을 전송함으로서, 해당 URl 에 접속시 저장된 이미지를 다운로드 하게 함
*
* */