package co.chatsdk.ui.chat.handlers;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import co.chatsdk.core.base.AbstractMessageViewHolder;
import co.chatsdk.core.dao.Message;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.MessageType;
import co.chatsdk.ui.R;
import co.chatsdk.ui.chat.viewholder.LocationMessageViewHolder;

public class LocationMessageDisplayHandler extends AbstractMessageDisplayHandler {
    @Override
    public void updateMessageCellView(Message message, AbstractMessageViewHolder viewHolder, Context context) {

    }

    @Override
    public String displayName(Message message) {
        return ChatSDK.shared().context().getString(R.string.location_message);
    }

    @Override
    public AbstractMessageViewHolder newViewHolder(boolean isReply, Activity activity) {
        View row = row(isReply, activity);
        return new LocationMessageViewHolder(row, activity);
    }
}
