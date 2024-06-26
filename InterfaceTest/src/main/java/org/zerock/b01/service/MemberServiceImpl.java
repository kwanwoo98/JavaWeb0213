package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.dto.MemberJoinDTO;
import org.zerock.b01.repository.MemberRepository;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
  private final ModelMapper modelMapper;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public boolean existMember(String mid) {
    // existsById() : 기본키(Primary key)를 이용하여 존재여부를 확인 메서드
    return memberRepository.existsById(mid);
  }

  @Override
  public MemberJoinDTO getMember(String mid) {
    Optional<Member> result = memberRepository.findById(mid);
    MemberJoinDTO joinDTO = modelMapper.map(result, MemberJoinDTO.class);
    return joinDTO;
  }

  @Override
  public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {
    //화면에서 가지고온 ID를 저장
    String mid = memberJoinDTO.getMid();
    //JPA에 지원하는 ID존재 여부 확인 메서드 실행
    boolean exist = memberRepository.existsById(mid);
    //아이디가 이미 존재하면 에러를 발생시키는 if문
    if(exist){
      throw new MidExistException();
    }
    //아이가 존재하지 않으면 Member객체로 변환
    Member member = modelMapper.map(memberJoinDTO, Member.class);
    //비밀번호 암호화
    member.changePassword(passwordEncoder.encode(memberJoinDTO.getMpw()));
    //권한 설정
    member.addRole(MemberRole.USER);
    log.info("============");
    log.info(member);
    log.info(member.getRoleSet());
    //데이터베이스에 저장
    memberRepository.save(member);
  }

  @Override
  public void modify(MemberJoinDTO memberDTO) {
    // 패스워드 암호화
    String mpw = passwordEncoder.encode(memberDTO.getMpw());
    //dto에 암호화된 비밀번호 설정
    memberDTO.setMpw(mpw);
    //dto를 entity로 변환
    Member member = modelMapper.map(memberDTO, Member.class);
    //데이터베이스에 저장
    memberRepository.save(member);
  }

  @Override
  public void remove(String mid) {
    memberRepository.deleteById(mid);
  }
  //  @Override
//  public void modify(String mpw, String mid) {
//    memberRepository.updatePassword(passwordEncoder.encode(mpw),mid);
//  }
}
