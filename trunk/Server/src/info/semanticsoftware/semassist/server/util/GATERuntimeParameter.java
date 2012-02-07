/*
    Semantic Assistants -- http://www.semanticsoftware.info/semantic-assistants

    This file is part of the Semantic Assistants architecture.

    Copyright (C) 2009, 2010 Semantic Software Lab, http://www.semanticsoftware.info
        Nikolaos Papadakis
        Tom Gitzinger

    The Semantic Assistants architecture is free software: you can
    redistribute and/or modify it under the terms of the GNU Affero General
    Public License as published by the Free Software Foundation, either
    version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package info.semanticsoftware.semassist.server.util;


import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.bind.annotation.XmlTransient;

import javax.jws.*;
import gate.*;


/**
 * Instances of this class are made available to the
 * client as part of a ServiceInfoForClient object,
 * so that the client knows which parameters it can
 * pass to a certain service, and which of those are
 * mandatory/mOptional.
 */
public class GATERuntimeParameter
{


     private boolean mOptional = true;

     // Can be ambiguous, therefore DEPRECATED
     // private String prClassname = "";

     /**
      * Name of the GATE pipeline the parameter is used for. Only
      * really needed if multiple pipelines are concatenated.
      */
     private String mPipelineName = "";
     private String mPrName = "";
     private String mParamName = "";
     private String mType = "";
     private String mDefaultValueString = null;
     private String mLabel = null;

     public static final String DOUBLE   = "double";
     public static final String INT      = "int";
     public static final String BOOLEAN  = "boolean";
     public static final String STRING   = "string";
     public static final String URL_TYPE = "url";
     public static final String CORPUS   = "corpus";
     

     // The actual parameter values
     private Double  mDoubleValue  = null;
     private String  mStringValue  = null;
     private Integer mIntValue     = null;
     private Boolean mBooleanValue = null;
     private URL     mUrlValue     = null;
     private Corpus  mCorpusValue  = null;
     

     public GATERuntimeParameter() 
	  {
	       
	  }
     
     public GATERuntimeParameter(GATERuntimeParameter other) 
	  {
	       this.mOptional           = other.mOptional;
	       this.mPrName             = other.mPrName;
	       this.mParamName          = other.mParamName;
	       this.mType               = other.mType;
	       this.mDefaultValueString = other.mDefaultValueString;
	       this.mLabel              = other.mLabel;
	       this.mDoubleValue        = other.mDoubleValue;
	       this.mStringValue        = other.mStringValue;
	       this.mIntValue           = other.mIntValue;
	       this.mBooleanValue       = other.mBooleanValue;
	       this.mUrlValue           = other.mUrlValue;
	       this.mCorpusValue        = other.mCorpusValue;
	       this.mPipelineName       = other.mPipelineName;
	  }
     

     /**
      * Checks if the types and values of the two runtime
      * parameters (this and other) match. Returns true
      * if they do, false otherwise.
      */
    public boolean valuesMatch( GATERuntimeParameter other )
    {
        if( !mType.equals( other.mType ) )
        {
            return false;
        }

        // Check the data types one after the other
        if( mType.equals( DOUBLE ) )
        {
            if( mDoubleValue == null && other.mDoubleValue == null )
            {
                return true;
            }
            return mDoubleValue.equals( other.mDoubleValue );
        }
        else if( mType.equals( INT ) )
        {
            if( mIntValue == null && other.mIntValue == null )
            {
                return true;
            }
            return mIntValue.equals( other.mIntValue );
        }
        else if( mType.equals( BOOLEAN ) )
        {
            if( mBooleanValue == null && other.mBooleanValue == null )
            {
                return true;
            }
            return mBooleanValue.equals( other.mBooleanValue );
        }
        else if( mType.equals( STRING ) )
        {
            if( mStringValue == null && other.mStringValue == null )
            {
                return true;
            }
            return mStringValue.equals( other.mStringValue );
        }
        else if( mType.equals( URL_TYPE ) )
        {
            if( mUrlValue == null && other.mUrlValue == null )
            {
                return true;
            }
            return mUrlValue.equals( other.mUrlValue );
        }

        return false;
    }

     @WebMethod()
    public boolean hasValue()
    {
        if( mType.equals( DOUBLE ) )
        {
            return mDoubleValue != null;
        }
        else if( mType.equals( INT ) )
        {
            return mIntValue != null;
        }
        else if( mType.equals( BOOLEAN ) )
        {
            return mBooleanValue != null;
        }
        else if( mType.equals( STRING ) )
        {
            return mStringValue != null;
        }
        else if( mType.equals( URL_TYPE ) )
        {
            return mUrlValue != null;
        }
        else if( mType.equals( CORPUS ) )
        {
            return mCorpusValue != null;
        }


        return false;
    }
     

     public void takeDefaultValue()
    {
        if( getDefaultValueString() == null )
        {
            return;
        }
        String s = getDefaultValueString();

        if( mType.equals( DOUBLE ) )
        {
            setDoubleValue( Double.parseDouble( s ) );
        }
        else if( mType.equals( INT ) )
        {
            setIntValue( Integer.parseInt( s ) );
        }
        else if( mType.equals( BOOLEAN ) )
        {
            setBooleanValue( Boolean.parseBoolean( s ) );
        }
        else if( mType.equals( STRING ) )
        {
            setStringValue( s );
        }
        else if( mType.equals( URL_TYPE ) )
        {
            try
            {
                setUrlValue( new URL( s ) );
            }
            catch( MalformedURLException e )
            {
            }
        }
        else if( mType.equals( CORPUS ) )
        {
        }


    }
     
     

     // Value setting methods. Note that we cannot overload as
     // this class will be used in web service context. Getters
     // and setters must "be of the same mType".
     public void setDoubleValue(Double d)
	  {
	       mDoubleValue  = d;
	       mStringValue  = null;
	       mIntValue     = null;
	       mBooleanValue = null;
	       mUrlValue     = null;
	       mCorpusValue  = null;
	  }

     public void setStringValue(String s) 
	  {
	       mStringValue  = s;
	       mDoubleValue  = null;
	       mIntValue     = null;
	       mBooleanValue = null;
	       mUrlValue     = null;
	       mCorpusValue  = null;
	  }
     public void setIntValue(Integer i) 
	  {
	       mIntValue       = i;
	       mStringValue    = null;
	       mDoubleValue    = null;
	       mBooleanValue   = null;
	       mUrlValue       = null;
	       mCorpusValue    = null;
	  }
     public void setBooleanValue(Boolean b) 
	  {
	       mBooleanValue = b;
	       mStringValue  = null;
	       mDoubleValue  = null;
	       mIntValue     = null;
	       mUrlValue     = null;
	       mCorpusValue  = null;
	  }
     public void setUrlValue(URL u) 
	  {
	       mUrlValue     = u;
	       mBooleanValue = null;
	       mStringValue  = null;
	       mDoubleValue  = null;
	       mIntValue     = null;
	       mCorpusValue  = null;
	  }
     public void setCorpusValue(Corpus c) 
	  {
	       mCorpusValue  = c;
	       mUrlValue     = null;
	       mBooleanValue = null;
	       mStringValue  = null;
	       mDoubleValue  = null;
	       mIntValue     = null;
	  }

     // Generic setValue
     public void setValue(Object value) 
	  {
          if( value == null )
          {
              mUrlValue     = null;
              mBooleanValue = null;
              mStringValue  = null;
              mDoubleValue  = null;
              mIntValue     = null;
              mCorpusValue  = null;
          }

          try
          {
              if( value instanceof Double )
              {
                  setDoubleValue( (Double) value );
              }
              else if( value instanceof Integer )
              {
                  setIntValue( (Integer) value );
              }
              else if( value instanceof Boolean )
              {
                  setBooleanValue( (Boolean) value );
              }
              else if( value instanceof String )
              {
                  setStringValue( (String) value );
              }
              else if( value instanceof URL )
              {
                  setUrlValue( (URL) value );
              }
              else if( value instanceof Corpus )
              {
                  setCorpusValue( (Corpus) value );
              }
          }
          catch( Exception e )
          {
              e.printStackTrace();
          }  
	  }


     /**
      * Takes a string and, according to the current
      * mType of this object, tries to parse it. If
      * successful, the obtained value is set to be
      * this parameter's value. For example, if the
      * current mType is DOUBLE, <code>Double.parseDouble()</code>
      * will be called on the argument, and the result
      * will be passed on to <code>setDoubleValue()</code>.
      */ 
     public void parseStringAndSetValue(String value) 
	  {

	       try
        {

            if( mType.equals( STRING ) )
            {
                // We can take it as is
                setStringValue( value );
            }
            else if( mType.equals( DOUBLE ) )
            {
                try
                {
                    value = value.replace( ",", "." );
                    double d = Double.parseDouble( value );
                    setDoubleValue( new Double( d ) );
                }
                catch( NumberFormatException e )
                {
                    Logging.exception( e );
                }
            }
            else if( mType.equals( INT ) )
            {
                try
                {
                    int i = Integer.parseInt( value );
                    setIntValue( new Integer( i ) );
                }
                catch( NumberFormatException e )
                {
                    Logging.exception( e );
                }
            }
            else if( mType.equals( BOOLEAN ) )
            {
                boolean b = Boolean.parseBoolean( value );
                setBooleanValue( new Boolean( b ) );
            }
            else if( mType.equals( GATERuntimeParameter.URL_TYPE ) )
            {
                try
                {
                    URL u = new URL( value );
                    setUrlValue( u );
                }
                catch( MalformedURLException e )
                {
                    Logging.exception( e );
                }
            }
            // TODO: add support for list types
            else
            {
                Logging.log( "GATERuntimeParameter::parseStringAndSetValue: parameter type \"" +
                             mType + "\" not recognized or not supported. Value not set." );
            }
        }
        catch( Exception e )
        {
        }


    }
     
     
     // Value getting
    public Double getDoubleValue()
    {
        return mDoubleValue;
    }

    public String getStringValue()
    {
        return mStringValue;
    }

    public Integer getIntValue()
    {
        return mIntValue;
    }

    public Boolean getBooleanValue()
    {
        return mBooleanValue;
    }

    public URL getUrlValue()
    {
        return mUrlValue;
    }

    @XmlTransient
    public Corpus getCorpusValue()
    {
        return mCorpusValue;
    }

    public Object getValueAsObject()
    {
        if( mType.equals( DOUBLE ) )
        {
            return mDoubleValue;
        }
        else if( mType.equals( STRING ) )
        {
            return mStringValue;
        }
        else if( mType.equals( BOOLEAN ) )
        {
            return mBooleanValue;
        }
        else if( mType.equals( INT ) )
        {
            return mIntValue;
        }
        else if( mType.equals( URL_TYPE ) )
        {
            return mUrlValue;
        }
        else if( mType.equals( CORPUS ) )
        {
            //Logging.log("---------------- Corpus value: " + mCorpusValue);
            return mCorpusValue;
        }

        Logging.log( "---------------- getValueAsObject(): No type information" );

        return null;
    }

    public boolean getOptional()
    {
        return mOptional;
    }

    public void setOptional( boolean o )
    {
        mOptional = o;
    }

    public String getPRName()
    {
        return mPrName;
    }

    public void setPRName( String s )
    {
        mPrName = s;
    }

    public String getPipelineName()
    {
        return mPipelineName;
    }

    public void setPipelineName( String s )
    {
        mPipelineName = s;
    }

    public String getParamName()
    {
        return mParamName;
    }

    public void setParamName( String n )
    {
        mParamName = n;
    }

    public String getType()
    {
        return mType;
    }

    public void setType( String t )
    {
        mType = t;
    }

    public String getDefaultValueString()
    {
        return mDefaultValueString;
    }

    public void setDefaultValueString( String d )
    {
        mDefaultValueString = d;
    }

    public String getLabel()
    {
        return mLabel;
    }

    public void setLabel( String l )
    {
        mLabel = l;
    }
}
