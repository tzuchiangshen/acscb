<!-- edited with XMLSPY v5 rel. 2 U (http://www.xmlspy.com) by Bogdan Jeram (E.S.O.) -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:acserr="Alma/ACSError">
	<xsl:output method="text" version="1.0" encoding="ASCII"/>
	<xsl:template match="/acserr:Type">
<xsl:text>/*******************************************************************************
* ALMA - Atacama Large Millimiter Array
* (c) European Southern Observatory, 2003 
*
*This library is free software; you can redistribute it and/or
*modify it under the terms of the GNU Lesser General Public
*License as published by the Free Software Foundation; either
*version 2.1 of the License, or (at your option) any later version.
*
*This library is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*Lesser General Public License for more details.
*
*You should have received a copy of the GNU Lesser General Public
*License along with this library; if not, write to the Free Software
*Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
*
* "@(#) $Id: AES2CPP.xslt,v 1.5 2012/01/27 11:00:07 bjeram Exp $"
*************  THIS FILE IS AUTOMATICALLY GENERATED !!!!!!
*/

#include "</xsl:text>
<xsl:value-of select="@name"/>
<xsl:text>.h"

</xsl:text>
		
<!--  ******************************************** Code or ErrorCode *************************************************************************************************************************** -->
<xsl:for-each select="acserr:Code | acserr:ErrorCode">
	<xsl:text>const char </xsl:text>
	<xsl:value-of select="../@name"/>
	<xsl:text>::</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>Completion::m_shortDescription[]="</xsl:text>
	<xsl:value-of select="@shortDescription"/>
	<xsl:text>";
</xsl:text>
    <xsl:text>bool </xsl:text>
    <xsl:value-of select="../@name"/>
    <xsl:text>::</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>Completion::isEqual(ACSErr::Completion &amp;completion) { return (completion.type == m_etype &amp;&amp; completion.code == m_code ); }
        
</xsl:text>
		</xsl:for-each>

<xsl:text>

</xsl:text>
<xsl:for-each select="acserr:ErrorCode[not (@_suppressExceptionGeneration)]">
	<xsl:text>const char </xsl:text>
	<xsl:value-of select="../@name"/>
	<xsl:text>::</xsl:text>
	<xsl:value-of select="@name"/>
	<xsl:text>ExImpl::m_shortDescription[]="</xsl:text>
	<xsl:value-of select="@shortDescription"/>
	<xsl:text>";
</xsl:text>
    <xsl:text>bool </xsl:text>
    <xsl:value-of select="../@name"/>
    <xsl:text>::</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>ExImpl::isEqual(ACSErr::ACSbaseExImpl &amp;ex) { return (ex.getErrorType() == m_etype &amp;&amp; ex.getErrorCode() == m_code ); }
        
</xsl:text>   
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>
