/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.handle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dspace.content.*;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

/**
 * Interface to the <a href="http://www.handle.net" target=_new>CNRI Handle
 * System </a>.
 *
 * <p>
 * Currently, this class simply maps handles to local facilities; handles which
 * are owned by other sites (including other DSpaces) are treated as
 * non-existent.
 * </p>
 *
 * @author Peter Breton
 * @version $Revision$
 */
public class HandleManager
{

    private static final Map<Character, String> charMap = new HashMap<Character, String>();

    static {
        charMap.put('А', "A");
        charMap.put('Б', "B");
        charMap.put('В', "V");
        charMap.put('Г', "G");
        charMap.put('Д', "D");
        charMap.put('Е', "E");
        charMap.put('Ё', "E");
        charMap.put('Ж', "Zh");
        charMap.put('З', "Z");
        charMap.put('И', "I");
        charMap.put('Й', "I");
        charMap.put('К', "K");
        charMap.put('Л', "L");
        charMap.put('М', "M");
        charMap.put('Н', "N");
        charMap.put('О', "O");
        charMap.put('П', "P");
        charMap.put('Р', "R");
        charMap.put('С', "S");
        charMap.put('Т', "T");
        charMap.put('У', "U");
        charMap.put('Ф', "F");
        charMap.put('Х', "H");
        charMap.put('Ц', "C");
        charMap.put('Ч', "Ch");
        charMap.put('Ш', "Sh");
        charMap.put('Щ', "Sh");
        charMap.put('Ъ', "'");
        charMap.put('Ы', "Y");
        charMap.put('Ь', "'");
        charMap.put('Э', "E");
        charMap.put('Ю', "U");
        charMap.put('Я', "Ya");
        charMap.put('а', "a");
        charMap.put('б', "b");
        charMap.put('в', "v");
        charMap.put('г', "g");
        charMap.put('д', "d");
        charMap.put('е', "e");
        charMap.put('ё', "e");
        charMap.put('ж', "zh");
        charMap.put('з', "z");
        charMap.put('и', "i");
        charMap.put('й', "i");
        charMap.put('к', "k");
        charMap.put('л', "l");
        charMap.put('м', "m");
        charMap.put('н', "n");
        charMap.put('о', "o");
        charMap.put('п', "p");
        charMap.put('р', "r");
        charMap.put('с', "s");
        charMap.put('т', "t");
        charMap.put('у', "u");
        charMap.put('ф', "f");
        charMap.put('х', "h");
        charMap.put('ц', "c");
        charMap.put('ч', "ch");
        charMap.put('ш', "sh");
        charMap.put('щ', "sh");
        charMap.put('ъ', "'");
        charMap.put('ы', "y");
        charMap.put('ь', "'");
        charMap.put('э', "e");
        charMap.put('ю', "u");
        charMap.put('я', "ya");

    }

    /** log4j category */
    private static Logger log = Logger.getLogger(HandleManager.class);

    /** Prefix registered to no one */
    static final String EXAMPLE_PREFIX = "123456789";

    /** Private Constructor */
    private HandleManager()
    {
    }

    /**
     * Return the local URL for handle, or null if handle cannot be found.
     *
     * The returned URL is a (non-handle-based) location where a dissemination
     * of the object referred to by handle can be obtained.
     *
     * @param context
     *            DSpace context
     * @param handle
     *            The handle
     * @return The local URL
     * @exception SQLException
     *                If a database error occurs
     */
    public static String resolveToURL(Context context, String handle)
            throws SQLException
    {
        TableRow dbhandle = findHandleInternal(context, handle);

        if (dbhandle == null)
        {
            return null;
        }

        String url = ConfigurationManager.getProperty("dspace.url")
                + "/handle/" + handle;

        if (log.isDebugEnabled())
        {
            log.debug("Resolved " + handle + " to " + url);
        }

        return url;
    }
    
    /**
     * Try to detect a handle in a URL.
     * @param context DSpace context
     * @param url The URL
     * @return The handle or null if the handle couldn't be extracted of a URL
     * or if the extracted handle couldn't be found.
     * @throws SQLException  If a database error occurs
     */
    public static String resolveUrlToHandle(Context context, String url)
            throws SQLException
    {
        String dspaceUrl = ConfigurationManager.getProperty("dspace.url")
                + "/handle/";
        String handleResolver = ConfigurationManager.getProperty("handle.canonical.prefix");
        
        String handle = null;
        
        if (url.startsWith(dspaceUrl))
        {
            handle = url.substring(dspaceUrl.length());
        }
        
        if (url.startsWith(handleResolver))
        {
            handle = url.substring(handleResolver.length());
        }
        
        if (null == handle)
        {
            return null;
        }
        
        // remove trailing slashes
        while (handle.startsWith("/"))
        {
            handle = handle.substring(1);
        }
        TableRow dbhandle = findHandleInternal(context, handle);
        
        return (null == dbhandle) ? null : handle;
    }

    /**
     * Transforms handle into the canonical form <em>hdl:handle</em>.
     *
     * No attempt is made to verify that handle is in fact valid.
     *
     * @param handle
     *            The handle
     * @return The canonical form
     */
    public static String getCanonicalForm(String handle)
    {

    	// Let the admin define a new prefix, if not then we'll use the
    	// CNRI default. This allows the admin to use "hdl:" if they want to or
    	// use a locally branded prefix handle.myuni.edu.
    	String handlePrefix = ConfigurationManager.getProperty("handle.canonical.prefix");
    	if (handlePrefix == null || handlePrefix.length() == 0)
    	{
    		handlePrefix = "http://hdl.handle.net/";
    	}

    	return handlePrefix + handle;
    }

    /**
     * Returns displayable string of the handle's 'temporary' URL
     * <em>http://hdl.handle.net/handle/em>.
     *
     * No attempt is made to verify that handle is in fact valid.
     *
     * @param handle The handle
     * @return The canonical form
     */

    //    public static String getURLForm(String handle)
    //    {
    //        return "http://hdl.handle.net/" + handle;
    //    }

    /**
     * Creates a new handle in the database.
     *
     * @param context
     *            DSpace context
     * @param dso
     *            The DSpaceObject to create a handle for
     * @return The newly created handle
     * @exception SQLException
     *                If a database error occurs
     */
    public static String createHandle(Context context, DSpaceObject dso)
            throws SQLException
    {
        TableRow handle = DatabaseManager.create(context, "Handle");
        String handleId = createId(handle.getIntColumn("handle_id"));

        if(dso instanceof Item) {
            try {
                Item i = (Item) dso;
                Metadatum[] dcorevalues2 = i.getMetadata("dc", "title", Item.ANY,
                        Item.ANY);

                Metadatum tit = dcorevalues2[0];

                Collection c = i.getOwningCollection();

                Metadatum[] dcorevalues3 = c.getMetadata("dc", "title", Item.ANY,
                        Item.ANY);

                Metadatum tit2 = dcorevalues3[0];

                String titleCol = tit2.value;

                String title = tit.value;
                title = title.replace(",", "");
                title = title.replace(".", "");
                title = title.replace(":", "");
                title = title.replace(";", "");
                title = title.replace("\"", "");
                title = title.replace("'", "");
                title = title.replace("-", "");
                title = title.replace("+", "");
                title = title.replace("=", "");
                title = title.replace("(", "");
                title = title.replace(")", "");
                title = title.replace("%", "");
                title = title.replace("?", "");
                title = title.replace("!", "");
                title = title.replace("^", "");
                title = title.replace("#", "");
                title = title.replace("<", "");
                title = title.replace(">", "");
                title = title.replace("\\", "");
                title = title.replace("~", "");
                title = title.replace("~", "");
                title = title.replace("@", "");
                title = title.replace("|", "");
                title = title.replace("$", "");
                title = title.replace("&", "");
                title = title.replace("*", "");
                title = title.replace("[", "");
                title = title.replace("]", "");
                title = title.replace("/", "");

                titleCol = titleCol.replace(",", "");
                titleCol = titleCol.replace(".", "");
                titleCol = titleCol.replace(":", "");
                titleCol = titleCol.replace(";", "");
                titleCol = titleCol.replace("\"", "");
                titleCol = titleCol.replace("'", "");
                titleCol = titleCol.replace("-", "");
                titleCol = titleCol.replace("+", "");
                titleCol = titleCol.replace("=", "");
                titleCol = titleCol.replace("(", "");
                titleCol = titleCol.replace(")", "");
                titleCol = titleCol.replace("%", "");
                titleCol = titleCol.replace("?", "");
                titleCol = titleCol.replace("!", "");
                titleCol = titleCol.replace("^", "");
                titleCol = titleCol.replace("#", "");
                titleCol = titleCol.replace("<", "");
                titleCol = titleCol.replace(">", "");
                titleCol = titleCol.replace("\\", "");
                titleCol = titleCol.replace("~", "");
                titleCol = titleCol.replace("~", "");
                titleCol = titleCol.replace("@", "");
                titleCol = titleCol.replace("|", "");
                titleCol = titleCol.replace("$", "");
                titleCol = titleCol.replace("&", "");
                titleCol = titleCol.replace("*", "");
                titleCol = titleCol.replace("[", "");
                titleCol = titleCol.replace("]", "");
                titleCol = titleCol.replace("/", "");

                if(title.length() > 200){
                    title = title.substring(0, 200);
                }

                handle.setColumn("handle", transliterate(titleCol).replace(" ", "-")+"/"+ transliterate(title).replace(" ", "-") +"-"+i.getID());
            } catch (Exception e) {

            }
        }
        if(dso instanceof Collection) {

            Collection i = (Collection) dso;
            Metadatum[] dcorevalues2 = i.getMetadata("dc", "title", Item.ANY,
                    Item.ANY);

            Metadatum tit = dcorevalues2[0];

           // unbindHandle(context, i);

            String title = tit.value;
            title = title.replace(",", "");
            title = title.replace(".", "");
            title = title.replace(":", "");
            title = title.replace(";", "");
            title = title.replace("\"", "");
            title = title.replace("'", "");
            title = title.replace("-", "");
            title = title.replace("+", "");
            title = title.replace("=", "");
            title = title.replace("(", "");
            title = title.replace(")", "");
            title = title.replace("%", "");
            title = title.replace("?", "");
            title = title.replace("!", "");
            title = title.replace("^", "");
            title = title.replace("#", "");
            title = title.replace("<", "");
            title = title.replace(">", "");
            title = title.replace("\\", "");
            title = title.replace("~", "");
            title = title.replace("~", "");
            title = title.replace("@", "");
            title = title.replace("|", "");
            title = title.replace("$", "");
            title = title.replace("&", "");
            title = title.replace("*", "");
            title = title.replace("[", "");
            title = title.replace("]", "");
            title = title.replace("/", "");

            if(title.length() > 200){
                title = title.substring(0, 200);
            }

            handle.setColumn("handle", transliterate(title).replace(" ", "-").replaceAll("-+", "-") +"/"+i.getID());

        }

        if(dso instanceof Community) {
            Community i = (Community) dso;
            Metadatum[] dcorevalues2 = i.getMetadata("dc", "title", Item.ANY,
                    Item.ANY);

           // Metadatum tit = dcorevalues2[0];

            // unbindHandle(context, i);

            String title = dso.getName();
            title = title.replace(",", "");
            title = title.replace(".", "");
            title = title.replace(":", "");
            title = title.replace(";", "");
            title = title.replace("\"", "");
            title = title.replace("'", "");
            title = title.replace("-", "");
            title = title.replace("+", "");
            title = title.replace("=", "");
            title = title.replace("(", "");
            title = title.replace(")", "");
            title = title.replace("%", "");
            title = title.replace("?", "");
            title = title.replace("!", "");
            title = title.replace("^", "");
            title = title.replace("#", "");
            title = title.replace("<", "");
            title = title.replace(">", "");
            title = title.replace("\\", "");
            title = title.replace("~", "");
            title = title.replace("~", "");
            title = title.replace("@", "");
            title = title.replace("|", "");
            title = title.replace("$", "");
            title = title.replace("&", "");
            title = title.replace("*", "");
            title = title.replace("[", "");
            title = title.replace("]", "");
            title = title.replace("/", "");

            if(title.length() > 200){
                title = title.substring(0, 200);
            }

            handle.setColumn("handle", transliterate(title).replace(" ", "-").replaceAll("-+", "-") +"/"+i.getID());
        }

        handle.setColumn("resource_type_id", dso.getType());
        handle.setColumn("resource_id", dso.getID());
        DatabaseManager.update(context, handle);

        if (log.isDebugEnabled())
        {
            log.debug("Created new handle for "
                    + Constants.typeText[dso.getType()] + " (ID=" + dso.getID() + ") " + handleId );
        }

        return handleId;
    }

    /**
     * Creates a handle entry, but with a handle supplied by the caller (new
     * Handle not generated)
     *
     * @param context
     *            DSpace context
     * @param dso
     *            DSpaceObject
     * @param suppliedHandle
     *            existing handle value
     * @return the Handle
     * @throws IllegalStateException if specified handle is already in use by another object
     */
    public static String createHandle(Context context, DSpaceObject dso,
            String suppliedHandle) throws SQLException, IllegalStateException
    {
        //Check if the supplied handle is already in use -- cannot use the same handle twice
        TableRow handle = findHandleInternal(context, suppliedHandle);
        if(handle!=null && !handle.isColumnNull("resource_id"))
        {
            //Check if this handle is already linked up to this specified DSpace Object
            if(handle.getIntColumn("resource_id")==dso.getID() &&
               handle.getIntColumn("resource_type_id")==dso.getType())
            {
                //This handle already links to this DSpace Object -- so, there's nothing else we need to do
                return suppliedHandle;
            }
            else
            {
                //handle found in DB table & already in use by another existing resource
                throw new IllegalStateException("Attempted to create a handle which is already in use: " + suppliedHandle);
            }
        }
        else if(handle!=null && !handle.isColumnNull("resource_type_id"))
        {
            //If there is a 'resource_type_id' (but 'resource_id' is empty), then the object using
            // this handle was previously unbound (see unbindHandle() method) -- likely because object was deleted
            int previousType = handle.getIntColumn("resource_type_id");

            //Since we are restoring an object to a pre-existing handle, double check we are restoring the same *type* of object
            // (e.g. we will not allow an Item to be restored to a handle previously used by a Collection)
            if(previousType != dso.getType())
            {
                throw new IllegalStateException("Attempted to reuse a handle previously used by a " +
                        Constants.typeText[previousType] + " for a new " +
                        Constants.typeText[dso.getType()]);
            }
        }
        else if(handle==null) //if handle not found, create it
        {
            //handle not found in DB table -- create a new table entry
            handle = DatabaseManager.create(context, "Handle");
            handle.setColumn("handle", suppliedHandle +"_testp[;");
        }

        handle.setColumn("resource_type_id", dso.getType());
        handle.setColumn("resource_id", dso.getID());
        DatabaseManager.update(context, handle);

        if (log.isDebugEnabled())
        {
            log.debug("Created new handle for "
                    + Constants.typeText[dso.getType()] + " (ID=" + dso.getID() + ") " + suppliedHandle );
        }

        return suppliedHandle;
    }

    /**
     * Removes binding of Handle to a DSpace object, while leaving the
     * Handle in the table so it doesn't get reallocated.  The AIP
     * implementation also needs it there for foreign key references.
     *
     * @param context DSpace context
     * @param dso DSpaceObject whose Handle to unbind.
     */
    public static void unbindHandle(Context context, DSpaceObject dso)
        throws SQLException
    {
        TableRowIterator rows = getInternalHandles(context, dso.getType(), dso.getID());
        if (rows != null)
        {
            while (rows.hasNext())
            {
                TableRow row = rows.next();
                //Only set the "resouce_id" column to null when unbinding a handle.
                // We want to keep around the "resource_type_id" value, so that we
                // can verify during a restore whether the same *type* of resource
                // is reusing this handle!
                row.setColumnNull("resource_id");
                DatabaseManager.update(context, row);

                if(log.isDebugEnabled())
                {
                    log.debug("Unbound Handle " + row.getStringColumn("handle") + " from object " + Constants.typeText[dso.getType()] + " id=" + dso.getID());
                }
            }
        }
        else
        {
            log.warn("Cannot find Handle entry to unbind for object " + Constants.typeText[dso.getType()] + " id=" + dso.getID());
        }
    }

    /**
     * Return the object which handle maps to, or null. This is the object
     * itself, not a URL which points to it.
     *
     * @param context
     *            DSpace context
     * @param handle
     *            The handle to resolve
     * @return The object which handle maps to, or null if handle is not mapped
     *         to any object.
     * @exception IllegalStateException
     *                If handle was found but is not bound to an object
     * @exception SQLException
     *                If a database error occurs
     */
    public static DSpaceObject resolveToObject(Context context, String handle)
            throws IllegalStateException, SQLException
    {
        TableRow dbhandle = findHandleInternal(context, handle);

        if (dbhandle == null)
        {
            //If this is the Site-wide Handle, return Site object
            if (handle.equals(Site.getSiteHandle()))
            {
                return Site.find(context, 0);
            }
            //Otherwise, return null (i.e. handle not found in DB)
            return null;
        }

        // check if handle was allocated previously, but is currently not
        // associated with a DSpaceObject
        // (this may occur when 'unbindHandle()' is called for an obj that was removed)
        if ((dbhandle.isColumnNull("resource_type_id"))
                || (dbhandle.isColumnNull("resource_id")))
        {
            //if handle has been unbound, just return null (as this will result in a PageNotFound)
            return null;
        }

        // What are we looking at here?
        int handletypeid = dbhandle.getIntColumn("resource_type_id");
        int resourceID = dbhandle.getIntColumn("resource_id");

        if (handletypeid == Constants.ITEM)
        {
            Item item = Item.find(context, resourceID);

            if (log.isDebugEnabled())
            {
                log.debug("Resolved handle " + handle + " to item "
                        + ((item == null) ? (-1) : item.getID()));
            }

            return item;
        }
        else if (handletypeid == Constants.COLLECTION)
        {
            Collection collection = Collection.find(context, resourceID);

            if (log.isDebugEnabled())
            {
                log.debug("Resolved handle " + handle + " to collection "
                        + ((collection == null) ? (-1) : collection.getID()));
            }

            return collection;
        }
        else if (handletypeid == Constants.COMMUNITY)
        {
            Community community = Community.find(context, resourceID);

            if (log.isDebugEnabled())
            {
                log.debug("Resolved handle " + handle + " to community "
                        + ((community == null) ? (-1) : community.getID()));
            }

            return community;
        }

        throw new IllegalStateException("Unsupported Handle Type "
                + Constants.typeText[handletypeid]);
    }

    /**
     * Return the handle for an Object, or null if the Object has no handle.
     *
     * @param context
     *            DSpace context
     * @param dso
     *            The object to obtain a handle for
     * @return The handle for object, or null if the object has no handle.
     * @exception SQLException
     *                If a database error occurs
     */
    public static String findHandle(Context context, DSpaceObject dso)
            throws SQLException
    {
        TableRowIterator rows = getInternalHandles(context, dso.getType(), dso.getID());
        if (rows == null || !rows.hasNext())
        {
            if (dso.getType() == Constants.SITE)
            {
                return Site.getSiteHandle();
            }
            else
            {
                return null;
            }
        }
        else
        {
            //TODO: Move this code away from the HandleManager & into the Identifier provider
            //Attempt to retrieve a handle that does NOT look like {handle.part}/{handle.part}.{version}
            String result = rows.next().getStringColumn("handle");
            while (rows.hasNext())
            {
                TableRow row = rows.next();
                //Ensure that the handle doesn't look like this 12346/213.{version}
                //If we find a match that indicates that we have a proper handle
                if(!row.getStringColumn("handle").matches(".*/.*\\.\\d+"))
                {
                    result = row.getStringColumn("handle");
                }
            }

            return result;
        }
    }

    /**
     * Return all the handles which start with prefix.
     *
     * @param context
     *            DSpace context
     * @param prefix
     *            The handle prefix
     * @return A list of the handles starting with prefix. The list is
     *         guaranteed to be non-null. Each element of the list is a String.
     * @exception SQLException
     *                If a database error occurs
     */
    public static List<String> getHandlesForPrefix(Context context, String prefix)
            throws SQLException
    {
        String sql = "SELECT handle FROM handle WHERE handle LIKE ? ";
        TableRowIterator iterator = DatabaseManager.queryTable(context, null, sql, prefix+"%");
        List<String> results = new ArrayList<String>();

        try
        {
            while (iterator.hasNext())
            {
                TableRow row = (TableRow) iterator.next();
                results.add(row.getStringColumn("handle"));
            }
        }
        finally
        {
            // close the TableRowIterator to free up resources
            if (iterator != null)
            {
                iterator.close();
            }
        }

        return results;
    }

    /**
     * Get the configured Handle prefix string, or a default
     * @return configured prefix or "123456789"
     */
    public static String getPrefix()
    {
        String prefix = ConfigurationManager.getProperty("handle.prefix");
        if (null == prefix)
        {
            prefix = EXAMPLE_PREFIX; // XXX no good way to exit cleanly
            log.error("handle.prefix is not configured; using " + prefix);
        }
        return prefix;
    }

    ////////////////////////////////////////
    // Internal methods
    ////////////////////////////////////////

    /**
     * Return the handle for an Object, or null if the Object has no handle.
     *
     * @param context
     *            DSpace context
     * @param type
     *            The type of object
     * @param id
     *            The id of object
     * @return The handle for object, or null if the object has no handle.
     * @exception SQLException
     *                If a database error occurs
     */
    private static TableRowIterator getInternalHandles(Context context, int type, int id)
            throws SQLException
    {
      	String sql = "SELECT * FROM Handle WHERE resource_type_id = ? " +
      				 "AND resource_id = ?";

	return DatabaseManager.queryTable(context, "Handle", sql, type, id);
    }

    /**
     * Find the database row corresponding to handle.
     *
     * @param context
     *            DSpace context
     * @param handle
     *            The handle to resolve
     * @return The database row corresponding to the handle
     * @exception SQLException
     *                If a database error occurs
     */
    private static TableRow findHandleInternal(Context context, String handle)
            throws SQLException
    {
        if (handle == null)
        {
            throw new IllegalArgumentException("Handle is null");
        }

        return DatabaseManager
                .findByUnique(context, "Handle", "handle", handle);
    }

    /**
     * Create a new handle id. The implementation uses the PK of the RDBMS
     * Handle table.
     *
     * @return A new handle id
     * @exception SQLException
     *                If a database error occurs
     */
    private static String createId(int id) throws SQLException
    {
        String handlePrefix = getPrefix();

        return new StringBuffer().append(handlePrefix).append(
                handlePrefix.endsWith("/") ? "" : "/").append(id).toString();
    }

    public static String transliterate(String string) {
        StringBuilder transliteratedString = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            Character ch = string.charAt(i);
            String charFromMap = charMap.get(ch);
            if (charFromMap == null) {
                transliteratedString.append(ch);
            } else {
                transliteratedString.append(charFromMap);
            }
        }
        return transliteratedString.toString();
    }
}
