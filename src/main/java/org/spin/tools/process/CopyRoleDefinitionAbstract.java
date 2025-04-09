/******************************************************************************
 * Product: ADempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2006-2017 ADempiere Foundation, All Rights Reserved.         *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * or (at your option) any later version.										*
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * or via info@adempiere.net or http://www.adempiere.net/license.html         *
 *****************************************************************************/

package org.spin.tools.process;

import org.compiere.process.SvrProcess;

/** Generated Process for (Copy Role from Client)
 *  @author ADempiere (generated) 
 *  @version Release 3.9.3
 */
public abstract class CopyRoleDefinitionAbstract extends SvrProcess {
	/** Process Value 	*/
	private static final String VALUE_FOR_PROCESS = "ECA25_CopyRole";
	/** Process Name 	*/
	private static final String NAME_FOR_PROCESS = "Copy Role from Client";
	/** Process Id 	*/
	private static final int ID_FOR_PROCESS = 54655;
	/**	Parameter Name for Template Client	*/
	public static final String ECA25_TEMPLATECLIENT_ID = "ECA25_TemplateClient_ID";
	/**	Parameter Name for Target Client	*/
	public static final String ECA25_TARGETCLIENT_ID = "ECA25_TargetClient_ID";
	/**	Parameter Value for Template Client	*/
	private int templateClientId;
	/**	Parameter Value for Target Client	*/
	private int targetClientId;

	@Override
	protected void prepare() {
		templateClientId = getParameterAsInt(ECA25_TEMPLATECLIENT_ID);
		targetClientId = getParameterAsInt(ECA25_TARGETCLIENT_ID);
	}

	/**	 Getter Parameter Value for Template Client	*/
	protected int getTemplateClientId() {
		return templateClientId;
	}

	/**	 Setter Parameter Value for Template Client	*/
	protected void setTemplateClientId(int templateClientId) {
		this.templateClientId = templateClientId;
	}

	/**	 Getter Parameter Value for Target Client	*/
	protected int getTargetClientId() {
		return targetClientId;
	}

	/**	 Setter Parameter Value for Target Client	*/
	protected void setTargetClientId(int targetClientId) {
		this.targetClientId = targetClientId;
	}

	/**	 Getter Parameter Value for Process ID	*/
	public static final int getProcessId() {
		return ID_FOR_PROCESS;
	}

	/**	 Getter Parameter Value for Process Value	*/
	public static final String getProcessValue() {
		return VALUE_FOR_PROCESS;
	}

	/**	 Getter Parameter Value for Process Name	*/
	public static final String getProcessName() {
		return NAME_FOR_PROCESS;
	}
}