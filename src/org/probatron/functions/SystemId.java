/*
 * This file is part of the source of
 * 
 * Probatron4J - a Schematron validator for Java(tm)
 * 
 * Copyright (C) 2010 Griffin Brown Digitial Publishing Ltd
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.probatron.functions;

import javax.xml.transform.Transformer;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.*;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.tree.iter.SingletonIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.probatron.NamespacePrefixMappings;
import org.probatron.Session;
import org.probatron.Utils;

@SuppressWarnings("serial")
public class SystemId extends ExtensionFunctionDefinition
{
    static Logger logger = LogManager.getLogger( SystemId.class.getName());

    private static StructuredQName funcName = new StructuredQName( "pr",
            Utils.PROBATRON_FUNCTION_NAME, "system-id" );


    public StructuredQName getFunctionQName()
    {
        return funcName;
    }


    public int getMinimumNumberOfArguments()
    {
        return 0;
    }


    public int getMaximumNumberOfArguments()
    {
        return 0;
    }


    public SequenceType[] getArgumentTypes()
    {
        return new SequenceType[] { SequenceType.SINGLE_STRING };
    }


    public SequenceType getResultType( SequenceType[] suppliedArgumentTypes )
    {
        return SequenceType.SINGLE_STRING;
    }


    public ExtensionFunctionCall makeCallExpression()
    {
        return new SystemIdCall();
    }

    private static class SystemIdCall extends ExtensionFunctionCall
    {

        public Sequence call(XPathContext context, Sequence[] arguments)
                throws XPathException
        {

            Session session =Session.sessionFromContext( context );
            logger.debug( "got Session " + session );
            String s = session.getValidationContext().getVerbatimName();

            return new StringValue( s );
        }

    }

}
