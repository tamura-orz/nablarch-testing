<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                
    <xsl:output method="html" encoding="UTF-8"/>

    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ja" >
            <head>
                <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
                <title>HTMLチェック結果</title>
                <style type="text/css">
                <xsl:comment>
                    html, body {
                        color: black;
                        overflow: auto;
                        height: 100%;
                        font: 80%/1.5 Verdana, "ＭＳ Ｐゴシック", sans-serif;
                    }

                    #mainContents {
                        height: 41em;
                        font-size: 1em;
                        overflow: auto;
                        z-index: 2;
                    }

                    table.data {
                        border: double 0.25em #000000;
                        font-size: 1em;
                        margin-top:2em;
                    }
                    
                    .data th {
                        border: double 0.25em #000000;
                        background-color:#ffff99;
                        padding:0.3em 1em;
                        text-align:left;
                        font-weight: bold;
                    }
                    
                    .data td {
                        border: double 0.25em #000000;
                        padding:0.3em 1em;
                        text-align:left;
                    }
                    
                    p.areaTitle {
                        text-align: left;
                        font-weight: bold;
                        font-size: 1em;
                    }
                </xsl:comment>
                </style>
                
            </head>
            <body>
                <div id="mainContents">
                　　<p class="areaTitle">HTMLチェック結果</p>
                    <table class="data">
                        <tr>
                            <th>
                                HTML名
                            </th>
                            <th>
                                エラー内容
                            </th>
                        </tr>
                        <xsl:for-each select="result/item">
                                <xsl:call-template name="htmlCheck" />
                        </xsl:for-each>
                    </table>
                </div>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="htmlCheck">
        <xsl:param name="tdFlag" select="0"/>
        
            <tr>
                <xsl:if test="$tdFlag=0">
                    <td>
                    <xsl:attribute name="rowspan">
                             <xsl:value-of select="count(./errors/error) + 1" />
                    </xsl:attribute>
                         <a>
                         <xsl:attribute name="href">
                             <xsl:value-of select="./path" />
                         </xsl:attribute>
                         <xsl:value-of select="./path"/>
                         </a>
                    </td>
                    <td>
                    <b>エラー件数 <xsl:value-of select="count(./errors/error)" /> 件</b>
                    </td>
                <xsl:param name="tdFlag" select="1" />
                </xsl:if>
            </tr>
            <xsl:for-each select="./errors/error">
            <tr>
                <td><xsl:value-of select="."/></td>
            </tr>
           </xsl:for-each>
    </xsl:template>
    
</xsl:stylesheet>
