package org.zhxie.sprintpoker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zhxie.sprintpoker.entity.TicketRecord;
import org.zhxie.sprintpoker.entity.dto.ResponseResult;
import org.zhxie.sprintpoker.service.TicketRecordService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  @Autowired
  private TicketRecordService ticketRecordService;
  @Autowired
  private HttpServletRequest request;

  @GetMapping()
  public List<TicketRecord> getDashboardRecords(Integer pageOffset, Integer limit) {
    return ticketRecordService.findAll(pageOffset, limit);
  }

  @PostMapping()
  public ResponseResult save(Principal user, @RequestBody  TicketRecord ticketRecord ) {
    ticketRecord.setCreator(user.getName());
    ticketRecordService.save(ticketRecord);
    return new ResponseResult(ResponseResult.SUCCESS, "保存成功！");
  }

//  @RequestMapping(method = RequestMethod.POST)
//  public ResponseResult save2(TicketRecord ticketRecord) {
//    ticketRecordService.save(ticketRecord);
//    return new ResponseResult(ResponseResult.SUCCESS, "保存成功！");
//  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseResult update(@PathVariable int id, @RequestBody TicketRecord ticketRecord) {
    Optional<TicketRecord> optional = ticketRecordService.findById(id);
    if (!optional.isPresent()) {
      return new ResponseResult(ResponseResult.FAIL, "记录不存在");
    }
    ticketRecord.setId(id);
    ticketRecordService.save(ticketRecord);
    return new ResponseResult(ResponseResult.SUCCESS, "修改成功！");
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseResult deleteById(@PathVariable int id) {
    ticketRecordService.deleteById(id);
    return new ResponseResult(ResponseResult.SUCCESS, "删除成功！");
  }

//  @RequestMapping(method = RequestMethod.DELETE)
//  public ResponseResult deleteByIds(@RequestBody List<Integer> ids) {
//    ticketRecordService.deleteByIds(ids);
//    return new ResponseResult(ResponseResult.SUCCESS, "删除成功！");
//  }

}