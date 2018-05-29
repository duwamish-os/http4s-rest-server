package schema

final case class ChatRequest(correlationId: String, utterance: String)
final case class ChatResponse(correlationId: String, displayText: String)
