package com.scalaone.smart.ext

import org.w3c.dom._

/**
 * Created by changhai on 15/3/19.
 */
class MockElement extends Element {
  override def getTagName: String = ???

  override def getAttributeNodeNS(namespaceURI: String, localName: String): Attr = ???

  override def removeAttributeNS(namespaceURI: String, localName: String): Unit = ???

  override def getElementsByTagName(name: String): NodeList = ???

  override def setAttributeNS(namespaceURI: String, qualifiedName: String, value: String): Unit = ???

  override def getAttribute(name: String): String = ???

  override def setIdAttributeNS(namespaceURI: String, localName: String, isId: Boolean): Unit = ???

  override def removeAttribute(name: String): Unit = ???

  override def hasAttribute(name: String): Boolean = ???

  override def getAttributeNode(name: String): Attr = ???

  override def setIdAttribute(name: String, isId: Boolean): Unit = ???

  override def hasAttributeNS(namespaceURI: String, localName: String): Boolean = ???

  override def setAttribute(name: String, value: String): Unit = ???

  override def getSchemaTypeInfo: TypeInfo = ???

  override def setAttributeNodeNS(newAttr: Attr): Attr = ???

  override def setIdAttributeNode(idAttr: Attr, isId: Boolean): Unit = ???

  override def removeAttributeNode(oldAttr: Attr): Attr = ???

  override def getAttributeNS(namespaceURI: String, localName: String): String = ???

  override def setAttributeNode(newAttr: Attr): Attr = ???

  override def getElementsByTagNameNS(namespaceURI: String, localName: String): NodeList = ???

  override def removeChild(oldChild: Node): Node = ???

  override def getBaseURI: String = ???

  override def getOwnerDocument: Document = ???

  override def getAttributes: NamedNodeMap = new MockNamedNodeMap

  override def hasChildNodes: Boolean = ???

  override def getNodeType: Short = ???

  override def compareDocumentPosition(other: Node): Short = ???

  override def getParentNode: Node = ???

  override def setUserData(key: String, data: scala.Any, handler: UserDataHandler): AnyRef = ???

  override def getPreviousSibling: Node = ???

  override def isSupported(feature: String, version: String): Boolean = ???

  override def hasAttributes: Boolean = ???

  override def isEqualNode(arg: Node): Boolean = ???

  override def lookupPrefix(namespaceURI: String): String = ???

  override def getFeature(feature: String, version: String): AnyRef = ???

  override def getNodeName: String = ???

  override def setPrefix(prefix: String): Unit = ???

  override def setTextContent(textContent: String): Unit = ???

  override def getNodeValue: String = ???

  override def isDefaultNamespace(namespaceURI: String): Boolean = ???

  override def replaceChild(newChild: Node, oldChild: Node): Node = ???

  override def appendChild(newChild: Node): Node = ???

  override def getNextSibling: Node = ???

  override def getPrefix: String = ???

  override def getChildNodes: NodeList = ???

  override def getLocalName: String = ???

  override def isSameNode(other: Node): Boolean = ???

  override def getLastChild: Node = ???

  override def setNodeValue(nodeValue: String): Unit = ???

  override def lookupNamespaceURI(prefix: String): String = ???

  override def insertBefore(newChild: Node, refChild: Node): Node = ???

  override def getTextContent: String = ???

  override def normalize(): Unit = ???

  override def cloneNode(deep: Boolean): Node = ???

  override def getNamespaceURI: String = ???

  override def getFirstChild: Node = ???

  override def getUserData(key: String): AnyRef = ???
}

class MockNamedNodeMap extends NamedNodeMap {
  override def getNamedItem(name: String): Node = ???

  override def getLength: Int = 0

  override def getNamedItemNS(namespaceURI: String, localName: String): Node = ???

  override def removeNamedItemNS(namespaceURI: String, localName: String): Node = ???

  override def removeNamedItem(name: String): Node = ???

  override def setNamedItem(arg: Node): Node = ???

  override def item(index: Int): Node = ???

  override def setNamedItemNS(arg: Node): Node = ???
}
