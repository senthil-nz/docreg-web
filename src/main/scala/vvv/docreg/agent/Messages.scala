package vvv.docreg.agent

import actors.Actor
import java.util.Date

// todo message type part of request case class? register handler for each type, gives case class encode and decode.
object MessageType extends Enumeration
{
  type Type = Value
  val registerRequest = Value(0)
  val registerReply = Value(1)
  val submitRequest = Value(4)
  val submitReply = Value(5)
  val editRequest = Value(6)
  val editReply = Value(7)
  val uneditRequest = Value(10)
  val subscribeRequest = Value(16)
  val subscribeReply = Value(17)
  val unsubscribeRequest = Value(18)
  val unsubscribeReply = Value(19)
  val nextChangeRequest = Value(21)
  val nextChangeReply = Value(22)
  val approvalRequest = Value(35)
  val approvalReply = Value(36)
}

case class Header(version: Int, message: MessageType.Type, transactionId: Int, sequence: Int)

case class DocumentInfo(key: Int, version: Int, fileName: String, projectName: String, title: String, description: String, access: String, author: String, date: String, server: String, client: String, editor: String, editorStart: String)

case class RevisionInfo(fileName: String, project: String, comment: String, access: String, author: String, date: Date, server: String, clientIp: String, clientHost: String, clientUserName: String, clientVersion: String, crc: String)

case class ApprovalInfo(fileName: String, approverName: String, approverEmail: String, status: String,  comment: String, date: Date, clientIp: String, clientHost: String, clientUserName: String)

case class SubscriberInfo(userName: String, email: String, emailEvent: String)

sealed abstract class Request
sealed abstract class Reply

case class RegisterRequest(fileName: String, project: String, comment: String, access: String, author: String, userName: String, clientHost: String, clientVersion: String) extends Request
case class RegisterReply(response: String, suggestedFileName: String) extends Reply

case class SubmitRequest(fileName: String, size: Int = -1) extends Request
case class SubmitReply(response: String, suggestedFileName: String) extends Reply

case class EditRequest(fileName: String, userName: String) extends Request
case class EditReply(userName: String) extends Reply

case class UneditRequest(fileName: String, userName: String) extends Request

case class NextChangeRequest(lastChange: Int) extends Request
case class NextChangeReply(changeNumber: Int, documentInfo: DocumentInfo) extends Reply

case class SubscribeRequest(fileName: String, userName: String, email: String, options: String) extends Request
case class SubscribeReply(response: String, fileName: String, userName: String) extends Reply

case class UnsubscribeRequest(fileName: String, userName: String, email: String) extends Request
case class UnsubscribeReply(response: String, fileName: String, userName: String) extends Reply

case class ApprovalRequest(fileName: String, approverUserName: String, approverEmail: String, status: String, comment: String, clientHost: String, userName: String) extends Request
case class ApprovalReply(response: String) extends Reply