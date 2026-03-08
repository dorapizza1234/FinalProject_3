package com.spring.app.chat.service;

import com.spring.app.chat.domain.ChatRoomDTO;
import com.spring.app.chat.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    // 의존성 주입 (Lombok의 @RequiredArgsConstructor 덕분에 자동으로 주입됩니다)
    private final ChatMapper chatMapper;

    @Override
    public List<ChatRoomDTO> getMyChatRooms(String loginUserEmail) {
        // 매퍼를 통해 DB에서 데이터를 조회해서 바로 리턴합니다.
        return chatMapper.selectMyChatRooms(loginUserEmail);
    }
    
    @Override
    public String getOrCreateRoom(int productNo, String sellerEmail, String buyerEmail) {
        // 1. 이미 존재하는 방이 있는지 조회
        String roomId = chatMapper.findRoomId(productNo, buyerEmail);
        
        // 2. 방이 없다면 새로 생성
        if (roomId == null) {
            roomId = UUID.randomUUID().toString(); // 고유한 방 ID 생성
            
            ChatRoomDTO newRoom = new ChatRoomDTO();
            newRoom.setRoomId(roomId);
            newRoom.setProductNo(productNo);
            newRoom.setSellerEmail(sellerEmail);
            newRoom.setBuyerEmail(buyerEmail);
            
            chatMapper.insertChatRoom(newRoom);
        }
        
        // 찾은 방 번호나 새로 만든 방 번호를 리턴
        return roomId;
    }
}