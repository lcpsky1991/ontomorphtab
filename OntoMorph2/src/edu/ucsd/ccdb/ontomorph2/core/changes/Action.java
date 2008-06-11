package edu.ucsd.ccdb.ontomorph2.core.changes;

/* Defines some kind of change that can be made within the framework that can be recorded
 * in revision control, can be undone, and can be redone.
 */
public abstract class Action {

	public abstract void undo();
	public abstract void doAction();
}
