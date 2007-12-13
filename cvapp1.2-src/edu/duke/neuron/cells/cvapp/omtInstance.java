package edu.duke.neuron.cells.cvapp;

/*
 
 Copyright (C) 2007 Christopher Aprea

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 	website: ontomorphtab.googlecode.com
 */


public class omtInstance 
{
	public String strName;
	public int beginPoint;
	public int endPoint;
	public int selectType=7;
	
	omtInstance()
	{
		strName = "";
		beginPoint = 0;
		endPoint = 0;
	}
	
	omtInstance(String name, int begin, int end)
	{
		strName = name;
		beginPoint = begin;
		endPoint = end;
	}
	
	public String toString()
	{
		return strName; 
	}
	
}
