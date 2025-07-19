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

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.*;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.tree.iter.SingletonIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.om.Sequence;

import org.probatron.Utils;

import com.griffinbrown.xmltool.utils.ISSN;
import com.griffinbrown.xmltool.utils.MalformedISSNException;

@SuppressWarnings("serial")
public class IsValidIssn extends ExtensionFunctionDefinition
{
    private static StructuredQName funcName = new StructuredQName( "pr",
            Utils.PROBATRON_FUNCTION_NAME, "is-valid-issn" );


    public StructuredQName getFunctionQName()
    {
        return funcName;
    }


    public int getMinimumNumberOfArguments()
    {
        return 1;
    }


    public int getMaximumNumberOfArguments()
    {
        return 1;
    }


    public SequenceType[] getArgumentTypes()
    {
        return new SequenceType[] { SequenceType.SINGLE_STRING };
    }


    public SequenceType getResultType( SequenceType[] suppliedArgumentTypes )
    {
        return SequenceType.SINGLE_BOOLEAN;
    }


    public ExtensionFunctionCall makeCallExpression()
    {
        return new IsValidIssnCall();
    }

    private static class IsValidIssnCall extends ExtensionFunctionCall
    {

        public Sequence call( XPathContext context, Sequence[] arguments )
                throws XPathException
        {
            boolean value = true;

            ISSN issn = null;
            try
            {
                //SequenceIterator iter = arguments[ 0 ];
                String candidate = arguments[ 0 ].toString();

                issn = new ISSN( candidate );
            }
            catch( MalformedISSNException m )
            {
                value = false;
            }

            if( issn != null && !issn.hasCorrectChecksum() )
            {
                value = false;
            }

            return value ? BooleanValue.TRUE : BooleanValue.FALSE ;
        }

    }

}
