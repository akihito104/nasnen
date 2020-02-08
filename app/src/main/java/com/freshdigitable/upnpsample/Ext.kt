package com.freshdigitable.upnpsample

import net.mm2d.upnp.util.asIterable
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

inline fun <R> NodeList.map(block: (Node) -> R): List<R> {
    return this.asIterable().map(block)
}

fun String.toXmlElements() :Element {
    return DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .parse(InputSource(StringReader(this)))
        .documentElement
}