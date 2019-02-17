/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:27 PM
 */

package co.chatsdk.ui.threads;

import android.app.AlertDialog;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import co.chatsdk.core.dao.Thread;
import co.chatsdk.core.events.NetworkEvent;
import co.chatsdk.core.interfaces.ThreadType;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.ui.R;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import co.chatsdk.ui.helpers.DialogUtils;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;

/**
 * Created by itzik on 6/17/2014.
 */
public class PublicThreadsFragment extends ThreadsFragment {


    @Override
    public void initViews() {
        super.initViews();

        Disposable d = adapter.onLongClickObservable().subscribe(thread -> {
            if (thread.getCreator() != null && thread.getCreator().isMe()) {
                DialogUtils.showToastDialog(getContext(), "", getResources().getString(R.string.alert_delete_thread), getResources().getString(R.string.delete),
                        getResources().getString(R.string.cancel), null, () -> {
                            ChatSDK.thread().deleteThread(thread)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new CompletableObserver() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                        }

                                        @Override
                                        public void onComplete() {
                                            adapter.clearData();
                                            reloadData();
                                            ToastHelper.show(getContext(), getString(R.string.delete_thread_success_toast));
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            ToastHelper.show(getContext(), getString(R.string.delete_thread_fail_toast));
                                        }
                                    });
                            return null;
                        });
            }
        });
    }

    @Override
    public Predicate<NetworkEvent> mainEventFilter() {
        return NetworkEvent.filterPublicThreadsUpdated();
    }

    @Override
    public boolean allowThreadCreation () {
        return ChatSDK.config().publicRoomCreationEnabled;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        /* Cant use switch in the library*/
        int id = item.getItemId();

        if (id == R.id.action_chat_sdk_add)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setTitle(getString(R.string.add_public_chat_dialog_title));

            // Set up the input
            final EditText input = new EditText(this.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton(getString(R.string.create), (dialog, which) -> {

                showOrUpdateProgressDialog(getString(R.string.add_public_chat_dialog_progress_message));
                final String threadName = input.getText().toString();

                ChatSDK.publicThread().createPublicThreadWithName(threadName)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((thread, throwable) -> {
                            if (throwable == null) {
                                dismissProgressDialog();
                                adapter.addRow(thread);

                                ToastHelper.show(getContext(), String.format(getString(R.string.public_thread__created), threadName));

                                ChatSDK.ui().startChatActivityForID(getContext(), thread.getEntityID());
                            }
                            else {
                                ChatSDK.logError(throwable);
                                Toast.makeText(PublicThreadsFragment.this.getContext(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                dismissProgressDialog();                            }
                        });

            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

            builder.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected List<Thread> getThreads() {
        return ChatSDK.thread().getThreads(ThreadType.Public);
    }
}
