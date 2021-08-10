/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.webdav.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.webdav.IMimeTyper;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.locking.ResourceLocks;
import org.apache.tomcat.util.http.fileupload.IOUtils;

public class DoGet extends DoHead {

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(DoGet.class);

    public DoGet(IWebdavStore store, String dftIndexFile, String insteadOf404,
            ResourceLocks resourceLocks, IMimeTyper mimeTyper,
            int contentLengthHeader) {
        super(store, dftIndexFile, insteadOf404, resourceLocks, mimeTyper,
                contentLengthHeader);

    }

    protected void doBody(ITransaction transaction, HttpServletResponse resp,
            String path) {

        try {
            StoredObject so = _store.getStoredObject(transaction, path);
            if (so.isNullResource()) {
                String methodsAllowed = DeterminableMethod
                        .determineMethodsAllowed(so);
                resp.addHeader("Allow", methodsAllowed);
                resp.sendError(WebdavStatus.SC_METHOD_NOT_ALLOWED);
                return;
            }
            OutputStream out = resp.getOutputStream();
            InputStream in = _store.getResourceContent(transaction, path);
            try {
                if (in != null) {
                    LOG.debug("开始 {}, ", path);
                    IOUtils.copyLarge(in, out);
                    LOG.debug("结束 {}", path);
                }
            } finally {
                // flushing causes a IOE if a file is opened on the webserver
                // client disconnected before server finished sending response
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (Exception e) {
                    LOG.warn("{} Closing InputStream causes Exception!\n", path
                            ,e);
                }
                try {
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    LOG.warn("{} Flushing OutputStream causes Exception!\n", path
                            ,e);
                }
            }
        } catch (Exception e) {
            LOG.warn("{} doBody causes Exception!\n", path
                    ,e);
            LOG.trace(e.toString());
        }
    }

    protected void folderBody(ITransaction transaction, String path,
            HttpServletResponse resp, HttpServletRequest req)
            throws IOException {

        StoredObject so = _store.getStoredObject(transaction, path);
        if (so == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, req
                    .getRequestURI());
        } else {

            if (so.isNullResource()) {
                String methodsAllowed = DeterminableMethod
                        .determineMethodsAllowed(so);
                resp.addHeader("Allow", methodsAllowed);
                resp.sendError(WebdavStatus.SC_METHOD_NOT_ALLOWED);
                return;
            }

            if (so.isFolder()) {
                // TODO some folder response (for browsers, DAV tools
                // use propfind) in html?
                Locale locale = req.getLocale();
                DateFormat shortDF= getDateTimeFormat(req.getLocale());
                resp.setContentType("text/html");
                resp.setCharacterEncoding("UTF8");
                OutputStream out = resp.getOutputStream();
                String[] children = _store.getChildrenNames(transaction, path);
                // Make sure it's not null
                children = children == null ? new String[] {} : children;
                // Sort by name
                Arrays.sort(children);
                StringBuilder childrenTemp = new StringBuilder();
                childrenTemp.append("<html><head><title>阿里网盘</title>");
                childrenTemp.append("<style type=\"text/css\">");
                childrenTemp.append(getCSS());

                childrenTemp.append("</style></head>");
                childrenTemp.append("<body>");
                childrenTemp.append("<h1 align=\"center\">阿里云网盘</h1>");
                childrenTemp.append("<h3 align=\"left\">当前目录    "+path+"</h3>");

                childrenTemp.append("<input type=\"text\" placeholder=\"搜索...\" id=\"myInput\" onkeyup=\"myFunction()\">");
                childrenTemp.append("<table id=\"myTable\">");
                childrenTemp.append("<tr><th>文件名称</th><th>文件大小</th><th>创建时间</th><th>修改时间</th></tr>");
                childrenTemp.append("<tr> <td colspan=\"4\"><a href=\"../\">上级目录</a></td></tr>");

                boolean isEven= false;
                for (String child : children)
                {
                    isEven= !isEven;
                    childrenTemp.append("<tr class=\"");
                    childrenTemp.append(isEven ? "even" : "odd");
                    childrenTemp.append("\">");
                    childrenTemp.append("<td>");
                    childrenTemp.append("<a href=\"");
                    childrenTemp.append(child);
                    StoredObject obj= _store.getStoredObject(transaction, path+"/"+child);
                    if (obj == null)
                    {
                        LOG.error("Should not return null for "+path+"/"+child);
                    }
                    if (obj != null && obj.isFolder())
                    {
                        childrenTemp.append("/");
                    }
                    childrenTemp.append("\">");
                    childrenTemp.append(child);
                    childrenTemp.append("</a></td>");
                    if (obj != null && obj.isFolder())
                    {
                        childrenTemp.append("<td>文件夹</td>");
                    }
                    else
                    {
                        childrenTemp.append("<td>");
                        if (obj != null )
                        {
                            childrenTemp.append(obj.getResourceLength());
                        }
                        else
                        {
                            childrenTemp.append("Unknown");
                        }
                        childrenTemp.append(" Bytes</td>");
                    }
                    if (obj != null && obj.getCreationDate() != null)
                    {
                        childrenTemp.append("<td>");
                        childrenTemp.append(shortDF.format(obj.getCreationDate()));
                        childrenTemp.append("</td>");
                    }
                    else
                    {
                        childrenTemp.append("<td></td>");
                    }
                    if (obj != null  && obj.getLastModified() != null)
                    {
                        childrenTemp.append("<td>");
                        childrenTemp.append(shortDF.format(obj.getLastModified()));
                        childrenTemp.append("</td>");
                    }
                    else
                    {
                        childrenTemp.append("<td></td>");
                    }
                    childrenTemp.append("</tr>");
                }
                childrenTemp.append("</table>");
                childrenTemp.append("</table>");
                childrenTemp.append("<script>");
                childrenTemp.append(getJS());
                childrenTemp.append("</script>");
                childrenTemp.append(getFooter(transaction, path, resp, req));
                childrenTemp.append("</body></html>");
                out.write(childrenTemp.toString().getBytes("UTF-8"));
            }
        }
    }

    /**
     * Return the CSS styles used to display the HTML representation
     * of the webdav content.
     *
     * @return
     */
    protected String getCSS()
    {
        // The default styles to use
       String retVal= " #myInput{\n" +
               " background: url('https://static.runoob.com/images/mix/searchicon.png')no-repeat;\n" +
               " background-position: 10px 12px;\n" +
               " width:100%;\n" +
               " padding: 12px 20px 12px 40px;\n" +
               " border:1px solid #ddd;\n" +
               " font-size: 16px;\n" +
               " margin-bottom: 12px;\n" +
               " border-radius: 6px;\n" +
               "}\n" +
               "#myTable {\n" +
               " width: 100%;\n" +
               " border: 1px solid #ddd;\n" +
               " font-size: 18px;\n" +
               " border-collapse:collapse;\n" +
               "}\n" +
               "#myTable th,td{\n" +
               " text-align: left;\n" +
               " padding:15px 12px;\n" +
               "}\n" +
               "#myTable tr{\n" +
               " /* 表格边框 */\n" +
               " border-bottom:1px solid #ddd;\n" +
               "}\n" +
               "#myTable tr:hover{\n" +
               " background-color: #f1f1f1;\n" +
               "}\n" +
               "#myTable th{\n" +
               " background-color: #f1f1f1;\n" +
               "}";
        return retVal;
    }



    protected String getJS()
    {
        // The default styles to use
        String retVal= "function myFunction() {\n" +
                " var myInput=document.getElementById(\"myInput\");\n" +
                " var filter=myInput.value.toUpperCase();\n" +
                " var table=document.getElementById(\"myTable\");\n" +
                " var tr=table.getElementsByTagName(\"tr\");\n" +
                " //循环列表每一项，查找匹配项\n" +
                " for(var i=0;i<tr.length;i++) {\n" +
                "  var td = tr[i].getElementsByTagName(\"td\")[0];\n" +
                "  if (td){\n" +
                "   if (td.innerHTML.toUpperCase().indexOf(filter) > -1) {\n" +
                "    tr[i].style.display = \"\";\n" +
                "   } else {\n" +
                "    tr[i].style.display = \"none\";\n" +
                "   }\n" +
                "  }\n" +
                " }\n" +
                "}";
        return retVal;
    }



    /**
     * Return the header to be displayed in front of the folder content
     *
     * @param transaction
     * @param path
     * @param resp
     * @param req
     * @return
     */
    protected String getHeader(ITransaction transaction, String path,
            HttpServletResponse resp, HttpServletRequest req)
    {
        return "<h1>Content of folder "+path+"</h1>";
    }

    /**
     * Return the footer to be displayed after the folder content
     *
     * @param transaction
     * @param path
     * @param resp
     * @param req
     * @return
     */
    protected String getFooter(ITransaction transaction, String path,
            HttpServletResponse resp, HttpServletRequest req)
    {
        return "";
    }

    /**
     * Return this as the Date/Time format for displaying Creation + Modification dates
     *
     * @param browserLocale
     * @return DateFormat used to display creation and modification dates
     */
    protected DateFormat getDateTimeFormat(Locale browserLocale)
    {
        return SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.MEDIUM, browserLocale);
    }
 }
