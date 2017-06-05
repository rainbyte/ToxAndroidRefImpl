package com.zoffcc.applications.trifa;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.l4digital.fastscroll.FastScroller;

import java.util.Iterator;
import java.util.List;

import static com.zoffcc.applications.trifa.TRIFAGlobals.TRIFA_MSG_TYPE.TRIFA_MSG_FILE;
import static com.zoffcc.applications.trifa.ToxVars.TOX_FILE_CONTROL.TOX_FILE_CONTROL_CANCEL;
import static com.zoffcc.applications.trifa.ToxVars.TOX_FILE_CONTROL.TOX_FILE_CONTROL_PAUSE;

public class MessagelistAdapter extends RecyclerView.Adapter implements FastScroller.SectionIndexer
{
    private static final String TAG = "trifa.MessagelistAdptr";

    private final List<Message> messagelistitems;
    private Context context;


    public MessagelistAdapter(Context context, List<Message> items)
    {
        Log.i(TAG, "MessagelistAdapter");

        this.messagelistitems = items;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Log.i(TAG, "MessageListHolder");

        View view = null;

        switch (viewType)
        {
            case Message_model.TEXT_INCOMING_NOT_READ:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_entry, parent, false);
                return new MessageListHolder_text_incoming_not_read(view, this.context);
            case Message_model.TEXT_INCOMING_HAVE_READ:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_self_entry_read, parent, false);
                return new MessageListHolder_text_incoming_read(view, this.context);

            case Message_model.TEXT_OUTGOING_NOT_READ:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_self_entry, parent, false);
                return new MessageListHolder_text_outgoing_not_read(view, this.context);
            case Message_model.TEXT_OUTGOING_HAVE_READ:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_self_entry_read, parent, false);
                return new MessageListHolder_text_outgoing_read(view, this.context);
        }

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_error, parent, false);
        return new MessageListHolder_error(view, this.context);
    }

    @Override
    public int getItemViewType(int position)
    {
        Message my_msg = this.messagelistitems.get(position);

        if (my_msg.TRIFA_MESSAGE_TYPE == TRIFA_MSG_FILE.value)
        {
            // FILE -------------
            if (my_msg.direction == 0)
            {
                // incoming file -----------
                if (my_msg.state == TOX_FILE_CONTROL_CANCEL.value)
                {
                    // ------- STATE: CANCEL -------------
                    return Message_model.FILE_INCOMING_STATE_CANCEL;
                    // ------- STATE: CANCEL -------------
                }
                else if (my_msg.state == TOX_FILE_CONTROL_PAUSE.value)
                {
                    // ------- STATE: PAUSE -------------
                    if (my_msg.ft_accepted == false)
                    {
                        // not yet accepted
                        return Message_model.FILE_INCOMING_STATE_PAUSE_NOT_YET_ACCEPTED;
                        // not yet accepted
                    }
                    else
                    {
                        // has accepted
                        return Message_model.FILE_INCOMING_STATE_PAUSE_HAS_ACCEPTED;
                        // has accepted
                    }
                    // ------- STATE: PAUSE -------------
                }
                else
                {
                    // ------- STATE: RESUME -------------
                    return Message_model.FILE_INCOMING_STATE_RESUME;
                    // ------- STATE: RESUME -------------
                }
            }
            else
            {
                // outgoing file -----------
                return Message_model.FILE_OUTGOING;
            }
            // FILE -------------
        }
        else
        {
            // TEXT -------------
            if (my_msg.direction == 0)
            {
                // msg to me
                if (my_msg.read)
                {
                    // has read
                    return Message_model.TEXT_INCOMING_HAVE_READ;
                }
                else
                {
                    // not yet read
                    return Message_model.TEXT_INCOMING_NOT_READ;
                }
                // msg to me
            }
            else
            {
                // msg from me
                if (my_msg.read)
                {
                    // has read
                    return Message_model.TEXT_OUTGOING_HAVE_READ;
                }
                else
                {
                    // not yet read
                    return Message_model.TEXT_OUTGOING_NOT_READ;
                }
                // msg from me
            }
            // TEXT -------------
        }

        // return Message_model.ERROR_UNKNOWN;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        Log.i(TAG, "onBindViewHolder:position=" + position);

        try
        {
            Message m2 = this.messagelistitems.get(position);

            switch (getItemViewType(position))
            {
                case Message_model.TEXT_INCOMING_NOT_READ:
                    ((MessageListHolder_text_incoming_not_read) holder).bindMessageList(m2);
                    break;
                case Message_model.TEXT_INCOMING_HAVE_READ:
                    ((MessageListHolder_text_incoming_read) holder).bindMessageList(m2);
                    break;
                case Message_model.TEXT_OUTGOING_NOT_READ:
                    ((MessageListHolder_text_outgoing_not_read) holder).bindMessageList(m2);
                    break;
                case Message_model.TEXT_OUTGOING_HAVE_READ:
                    ((MessageListHolder_text_outgoing_read) holder).bindMessageList(m2);
                    break;
                default:
                    ((MessageListHolder_error) holder).bindMessageList(null);
                    break;
            }
        }
        catch (Exception e)
        {
            Log.i(TAG, "onBindViewHolder:EE1:" + e.getMessage());
            e.printStackTrace();
            ((MessageListHolder_error) holder).bindMessageList(null);
        }
    }

    @Override
    public int getItemCount()
    {
        Log.i(TAG, "getItemCount:" + this.messagelistitems.size());
        return this.messagelistitems.size();
    }

    public void add_list_clear(List<Message> new_items)
    {
        Log.i(TAG, "add_list_clear:" + new_items);

        try
        {
            Log.i(TAG, "add_list_clear:001:new_items=" + new_items);
            this.messagelistitems.clear();
            this.messagelistitems.addAll(new_items);
            this.notifyDataSetChanged();
            Log.i(TAG, "add_list_clear:002");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.i(TAG, "add_list_clear:EE:" + e.getMessage());
        }
    }

    public void add_item(Message new_item)
    {
        Log.i(TAG, "add_item:" + new_item + ":" + this.messagelistitems.size());

        try
        {
            this.messagelistitems.add(new_item);
            this.notifyDataSetChanged();
            Log.i(TAG, "add_item:002:" + this.messagelistitems.size());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.i(TAG, "add_item:EE:" + e.getMessage());
        }
    }

    public void clear_items()
    {
        this.messagelistitems.clear();
        this.notifyDataSetChanged();
    }

    public boolean update_item(Message new_item)
    {
        Log.i(TAG, "update_item:" + new_item);

        boolean found_item = false;

        try
        {
            Iterator it = this.messagelistitems.iterator();
            while (it.hasNext())
            {
                Message m2 = (Message) it.next();

                if (m2.id == new_item.id)
                {
                    found_item = true;
                    int pos = this.messagelistitems.indexOf(m2);
                    Log.i(TAG, "update_item:003:" + pos);
                    this.messagelistitems.set(pos, new_item);
                    break;
                }
            }

            this.notifyDataSetChanged();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.i(TAG, "update_item:EE:" + e.getMessage());
        }

        return found_item;
    }

    @Override
    public String getSectionText(int position)
    {
        return "_A_";
    }
}