package edu.ucsd.ccdb.ontomorph2.view.gui2d;

import org.fenggui.menu.Menu;
import org.fenggui.menu.MenuItem;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;

/**
 * A menuItem that retains a reference to a Tangible that this menu item corresponds to. 
 * Execution of this MenuItem should act on its tangible
 * 
 * @author caprea
 *
 */
public class ContextMenuItem extends MenuItem 
{
	Tangible reference = null;

	public ContextMenuItem(String title)
	{
		super(title);
		reference = null;
	}

	public ContextMenuItem(String title, Tangible tan)
	{
		super(title);
		reference = tan;
	}

	public Tangible getTangible()
	{
		return reference;
	}
}
