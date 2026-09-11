package com.guardianescolar.api.modules.auth.service;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    public String plainAction(String title, String description, String buttonText, String url, String note) {
        return title + "\n\n"
                + description + "\n\n"
                + buttonText + ": " + url + "\n\n"
                + note;
    }

    public String plainCode(String title, String description, String code, String note) {
        return title + "\n\n"
                + description + "\n\n"
                + "Código: " + code + "\n\n"
                + note;
    }

    public String htmlAction(String title, String description, String buttonText, String url, String note) {
        String safeTitle = escapeHtml(title);
        String safeDescription = escapeHtml(description);
        String safeButton = escapeHtml(buttonText);
        String safeUrl = escapeHtml(url);
        String safeNote = escapeHtml(note);

        return """
                <!doctype html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0;padding:0;background:#f3f6fb;font-family:Arial,Helvetica,sans-serif;color:#172033;">
                  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#f3f6fb;padding:28px 12px;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:560px;background:#ffffff;border:1px solid #dfe7f2;border-radius:14px;overflow:hidden;">
                          <tr>
                            <td style="background:#155a9c;padding:24px 28px;text-align:center;">
                              <div style="font-size:13px;font-weight:700;letter-spacing:.08em;text-transform:uppercase;color:#bfe3ff;">GPS Guardian Escolar</div>
                              <div style="margin-top:8px;font-size:26px;line-height:1.25;font-weight:800;color:#ffffff;">%s</div>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:30px 28px 26px;">
                              <p style="margin:0 0 22px;font-size:16px;line-height:1.55;color:#334155;">%s</p>
                              <table role="presentation" cellspacing="0" cellpadding="0" style="margin:0 auto 24px;">
                                <tr>
                                  <td style="border-radius:10px;background:#1f5f9f;">
                                    <a href="%s" style="display:inline-block;padding:14px 24px;font-size:15px;font-weight:700;color:#ffffff;text-decoration:none;border-radius:10px;">%s</a>
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:20px 0 0;font-size:13px;line-height:1.5;color:#64748b;">%s</p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:16px 28px;background:#f8fafc;text-align:center;font-size:12px;color:#64748b;">
                              Mensaje automático. No respondas a este email.
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(safeTitle, safeDescription, safeUrl, safeButton, safeNote);
    }

    public String htmlCode(String title, String description, String code, String note) {
        String safeTitle = escapeHtml(title);
        String safeDescription = escapeHtml(description);
        String safeCode = escapeHtml(code);
        String safeNote = escapeHtml(note);

        return """
                <!doctype html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0;padding:0;background:#f3f6fb;font-family:Arial,Helvetica,sans-serif;color:#172033;">
                  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#f3f6fb;padding:28px 12px;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:560px;background:#ffffff;border:1px solid #dfe7f2;border-radius:14px;overflow:hidden;">
                          <tr>
                            <td style="background:#155a9c;padding:24px 28px;text-align:center;">
                              <div style="font-size:13px;font-weight:700;letter-spacing:.08em;text-transform:uppercase;color:#bfe3ff;">GPS Guardian Escolar</div>
                              <div style="margin-top:8px;font-size:26px;line-height:1.25;font-weight:800;color:#ffffff;">%s</div>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:30px 28px 26px;text-align:center;">
                              <p style="margin:0 0 24px;font-size:16px;line-height:1.55;color:#334155;text-align:left;">%s</p>
                              <div style="display:inline-block;padding:16px 30px;border-radius:12px;background:#1f5f9f;color:#ffffff;font-size:22px;font-weight:800;letter-spacing:0.08em;">%s</div>
                              <p style="margin:24px 0 0;font-size:13px;line-height:1.5;color:#64748b;text-align:left;">%s</p>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:16px 28px;background:#f8fafc;text-align:center;font-size:12px;color:#64748b;">
                              Mensaje automático. No respondas a este email.
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(safeTitle, safeDescription, safeCode, safeNote);
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
