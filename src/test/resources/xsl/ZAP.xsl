<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
	<xsl:output method="xml" indent="yes" encoding="UTF-8" />

	<xsl:key name="risk" match="alert" use="risk" />
	<xsl:key name="alert" match="alert" use="alert" />

	<xsl:template match="/alerts">
		<html>
			<head>
				<title>Zap Reports</title>
				<style>
					html, body, div, span, applet, object, iframe,
					h1, h2, h3, h4, h5, h6, p, blockquote, pre,
					a, abbr, acronym, address, big, cite, code,
					del, dfn, em, font, img, ins, kbd, q, s, samp,
					small, strike, strong, sub, sup, tt, var,
					b, u, i, center,
					dl, dt, dd, ol, ul, li,
					fieldset, form, label, legend,
					table, caption, tbody, tfoot, thead, tr, th, td {
						margin: 0;
						padding: 0;
						border: 0;
						outline: 0;
						font-size: 100%;
						vertical-align: baseline;
						background: transparent;
					}
					body {
						line-height: 1;
					}
					ol, ul {
						list-style: none;
					}
					blockquote, q {
						quotes: none;
					}
					blockquote:before, blockquote:after,
					q:before, q:after {
						content: '';
						content: none;
					}

					/* remember to define focus styles! */
					:focus {
						outline: 0;
					}

					/* remember to highlight inserts somehow! */
					ins {
						text-decoration: none;
					}
					del {
						text-decoration: line-through;
					}

					/* tables still need 'cellspacing="0"' in the markup */
					table {
						border-collapse: collapse;
						border-spacing: 0;
					}

					/* Main styles */
					.bReport {
						font: 12px Arial, sans-serif;
						padding: 20px;
					}

					.bReport__eTitle {
						font-size: 30px;
						text-align: center;
					}

					.bReport__eTable {
						width: 100%;
						margin-top: 20px;
					}

					.bReport__eTable__mInner {
						margin: 0;
					}

					.bReport__eTable__mInner .bReport__eTable__eItem {
						border: none;
					}

					.bReport__eTable__mInner .bReport__eTable__eItems:nth-child(odd) .bReport__eTable__eItem {
						background-color: #f9f9f9;
					}

					.bReport__eTable__mInner .bReport__eTable__eItems__mLast .bReport__eTable__eItem {
						border-bottom: 1px #ddd solid;
					}

					.bReport__eTable__mInner .bReport__eTable__eItems__mEnd .bReport__eTable__eItem {
						border-bottom: none;
						background: none !important;
					}

					.bReport__eTable__eItems__mLevelInformational .bReport__eTable__eItem__mRisk {
						color: #31708f;
						background: #d9edf7;
					}

					.bReport__eTable__eItems__mLevelLow .bReport__eTable__eItem__mRisk {
						color: #8a6d3b;
						background: #fcf8e3;
					}

					.bReport__eTable__eItems__mLevelMedium .bReport__eTable__eItem__mRisk {
						color: #3c763d;
						background: #dff0d8;
					}

					.bReport__eTable__eItems__mLevelHigh .bReport__eTable__eItem__mRisk {
						color: #a94442;
						background: #f2dede;
					}

					.bReport__eTable__eItem {
						border: 1px #ddd solid;
						padding: 10px;
					}

					.bReport__eTable__eItem__mComposite {
						width: 50%;
						padding: 0;
					}

					.bReport__eTable__eItem__mLast {
						border: none !important;
					}

					.bReport__eTable__eItem__mHead {
						font-size: 18px;
						border-bottom-width: 2px;
					}

					.bReport__eTable__eItem__mRisk {
						text-align: center;
						vertical-align: top;
					}

					.bReport__eTable__eItem__mUrl,
					.bReport__eTable__eItem__mAttack {
						font-family: monospace;
						word-break: break-all;
					}

					.bReport__eTable__eItem__mUrl {
						vertical-align: middle;
					}

					.bReport__eTable__eItem__mAlert {
						font-size: 16px;
						vertical-align: top;
					}

					.bReport__eTable__eItem__eDesc {
						font-size: 12px;
						color: #666;
						border-top: 1px #ddd solid;
						margin: 10px 0 0;
						padding: 10px 0;
					}

					.bReport__eTable__eItem__eAdditionalInfo {
						margin-top: 10px;
					}

					.bReport__eTable__eItem__eCode {
						padding: 2px 4px;
						font-size: 90%;
						color: #c7254e;
						word-break: break-all;
						background-color: #f9f2f4;
						border-radius: 4px;
					}

					.bReport__eTable__eItem__eCode__mBadge {
						background-color: #faebcc;
					}

					.bReport__eTable__eButton {
						font-size: 12px;
						color: #fff;
						line-height: 1.5;
						text-align: center;
						white-space: nowrap;
						vertical-align: middle;
						border: 1px solid #357ebd;
						border-radius: 3px;
						background-color: #428bca;
						-webkit-user-select: none;
						-moz-user-select: none;
						-ms-user-select: none;
						-o-user-select: none;
						user-select: none;
						padding: 1px 5px;
						cursor: pointer;
						display: inline-block;
					}

					.bReport__eTable__eButton:hover {
						color: #fff;
						background-color: #3276b1;
						border-color: #285e8e;
					}

					.bReport__eTable__eButton__mCollapse {
						margin-top: 10px;
						display: none;
					}

					.bReport__eTable__eItems__mCollapsible .bReport__eTable__eButton__mCollapse {
						display: inline-block;
					}

					.bReport__eTable__eButton__eText__mExpand,
					.bReport__eTable__eItems__mCollapse .bReport__eTable__eButton__eText__mCollapse,
					.bReport__eTable__eItems__mCollapse .bReport__eTable__mInner .bReport__eTable__eItems__mLast {
						display: none;
					}

					.bReport__eTable__eButton__eText__mCollapse,
					.bReport__eTable__eItems__mCollapse .bReport__eTable__eButton__eText__mExpand {
						display: inline;
					}

					.bReport__eTable__eItems__mCollapse .bReport__eTable__mInner .bReport__eTable__eItems__mLast:first-child {
						display: table-row;
					}
				</style>
				<script src="//code.jquery.com/jquery-2.0.3.min.js" type="text/javascript">//</script>
				<script type="text/javascript">
					<xsl:comment>
						<![CDATA[
							$(function() {
								$('.bReport__eTable__mInner').each(function(i, item) {
									var list = $(item);

									list
										.closest('.bReport__eTable__eItems__mParent')
										.toggleClass('bReport__eTable__eItems__mCollapsible', list.find('.bReport__eTable__eItems').length > 2);
								});

								$('.bReport__eTable__eItems__mCollapsible .bReport__eTable__eButton')
									.on('click', function(event) {
										$(event.currentTarget)
											.closest('.bReport__eTable__eItems__mParent')
											.toggleClass('bReport__eTable__eItems__mCollapse');
									})
									.closest('.bReport__eTable__eItems__mParent')
									.addClass('bReport__eTable__eItems__mCollapse');
							});
						]]>
					</xsl:comment>
				</script>
			</head>
			<body>
				<div class="bReport">
					<h1 class="bReport__eTitle">Report Alerts</h1>
					<table class="bReport__eTable">
						<thead>
							<tr>
								<th class="bReport__eTable__eItem bReport__eTable__eItem__mHead">Risk</th>
								<th class="bReport__eTable__eItem bReport__eTable__eItem__mHead">Alert</th>
								<th class="bReport__eTable__eItem bReport__eTable__eItem__mHead">URL</th>
							</tr>
						</thead>
						<tbody>
							<xsl:apply-templates select="alert[generate-id(.)=generate-id(key('alert',alert)[1]) and starts-with(risk, 'High')]"/>
							<xsl:apply-templates select="alert[generate-id(.)=generate-id(key('alert',alert)[1]) and starts-with(risk, 'Medium')]"/>
							<xsl:apply-templates select="alert[generate-id(.)=generate-id(key('alert',alert)[1]) and starts-with(risk, 'Low')]"/>
							<xsl:apply-templates select="alert[generate-id(.)=generate-id(key('alert',alert)[1]) and starts-with(risk, 'Informational')]"/>
						</tbody>
					</table>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="alert">
		<tr>
			<xsl:attribute name="class">
				<xsl:text>bReport__eTable__eItems</xsl:text>
				<xsl:text> bReport__eTable__eItems__mParent</xsl:text>
				<xsl:text> bReport__eTable__eItems__mLevel</xsl:text>
				<xsl:value-of select="risk" />
			</xsl:attribute>
			<td class="bReport__eTable__eItem bReport__eTable__eItem__mRisk">
				<div>
					<xsl:value-of select="risk" />
				</div>
				<button type="button" class="bReport__eTable__eButton bReport__eTable__eButton__mCollapse">
					<span class="bReport__eTable__eButton__eText bReport__eTable__eButton__eText__mExpand">Expand</span>
					<span class="bReport__eTable__eButton__eText bReport__eTable__eButton__eText__mCollapse">Collapse</span>
				</button>
			</td>
			<td class="bReport__eTable__eItem bReport__eTable__eItem__mAlert">
				<xsl:value-of select="alert" />
				<p class="bReport__eTable__eItem__eDesc">
					<xsl:value-of select="description" />
				</p>
			</td>
			<td class="bReport__eTable__eItem bReport__eTable__eItem__mComposite">
				<table class="bReport__eTable bReport__eTable__mInner">
					<xsl:for-each select="key('alert', alert)">
						<tr class="bReport__eTable__eItems bReport__eTable__eItems__mLast">
							<td class="bReport__eTable__eItem bReport__eTable__eItem__mUrl">
								<xsl:if test="evidence != ''">
									<xsl:value-of select="url" />
									<p class="bReport__eTable__eItem__eAdditionalInfo">
										<span title="evidence" class="bReport__eTable__eItem__eCode bReport__eTable__eItem__eCode__mBadge">
											<xsl:value-of select="evidence" />
										</span>
									</p>
								</xsl:if>
								<xsl:if test="evidence = ''">
									<xsl:value-of select="url" />
								</xsl:if>
								<xsl:if test="attack != ''">
									<p class="bReport__eTable__eItem__eAdditionalInfo">
										<span title="attack" class="bReport__eTable__eItem__eCode">
											<xsl:value-of select="param" />
											<xsl:text>: </xsl:text>
											<xsl:value-of select="attack" />
										</span>
									</p>
								</xsl:if>
							</td>
						</tr>
					</xsl:for-each>
					<tr class="bReport__eTable__eItems bReport__eTable__eItems__mEnd">
						<td class="bReport__eTable__eItem">
							<button type="button" class="bReport__eTable__eButton bReport__eTable__eButton__mCollapse">
								<span class="bReport__eTable__eButton__eText bReport__eTable__eButton__eText__mExpand">Expand URLs List</span>
								<span class="bReport__eTable__eButton__eText bReport__eTable__eButton__eText__mCollapse">Collapse URLs List</span>
							</button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>