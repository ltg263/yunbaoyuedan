.
├── app
│   ├── debug
│   │   ├── app-debug.apk
│   │   └── output.json
│   ├── release
│   │   ├── app-release.apk
│   │   └── output.json
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           ├── ic_launcher-web.png
│           └── java
│               └── com
│                   └── yunbao
│                       └── shortvideo
│                           ├── AppContext.java
│                           ├── activity
│                           │   └── LauncherActivity.java
│                           └── wxapi
│                               ├── WXEntryActivity.java
│                               └── WXPayEntryActivity.java
├── beauty
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           └── java
│               └── com
│                   └── yunbao
│                       └── beauty
│                           ├── adapter
│                           │   ├── MeiYanTitleAdapter.java
│                           │   ├── MhHaHaAdapter.java
│                           │   ├── MhMakeupAdapter.java
│                           │   ├── MhMeiYanAdapter.java
│                           │   ├── MhMeiYanFilterAdapter.java
│                           │   ├── MhMeiYanOneKeyAdapter.java
│                           │   ├── MhTeXiaoActionAdapter.java
│                           │   ├── MhTeXiaoSpecialAdapter.java
│                           │   ├── MhTeXiaoWaterAdapter.java
│                           │   ├── SimpleFilterAdapter.java
│                           │   ├── TieZhiAdapter.java
│                           │   └── TieZhiTitleAdapter.java
│                           ├── bean
│                           │   ├── HaHaBean.java
│                           │   ├── MakeupBean.java
│                           │   ├── MeiYanBean.java
│                           │   ├── MeiYanDataBean.java
│                           │   ├── MeiYanFilterBean.java
│                           │   ├── MeiYanOneKeyBean.java
│                           │   ├── MeiYanOneKeyValue.java
│                           │   ├── MeiYanTypeBean.java
│                           │   ├── MeiYanValueBean.java
│                           │   ├── SimpleFilterBean.java
│                           │   ├── TeXiaoActionBean.java
│                           │   ├── TeXiaoSpecialBean.java
│                           │   ├── TeXiaoWaterBean.java
│                           │   ├── TieZhiBean.java
│                           │   └── TieZhiTypeBean.java
│                           ├── constant
│                           │   └── Constants.java
│                           ├── custom
│                           │   ├── GridSpacingItemDecoration.java
│                           │   └── TextSeekBar.java
│                           ├── event
│                           │   └── TieZhiCancelEvent.java
│                           ├── glide
│                           │   └── ImgLoader.java
│                           ├── interfaces
│                           │   ├── CommonCallback.java
│                           │   ├── IBeautyClickListener.java
│                           │   ├── IBeautyEffectListener.java
│                           │   ├── IBeautyViewHolder.java
│                           │   ├── OnBottomHideListener.java
│                           │   ├── OnBottomShowListener.java
│                           │   ├── OnCaptureListener.java
│                           │   ├── OnItemClickListener.java
│                           │   ├── OnTieZhiActionClickListener.java
│                           │   ├── OnTieZhiActionDownloadListener.java
│                           │   ├── OnTieZhiActionListener.java
│                           │   └── OnTieZhiClickListener.java
│                           ├── utils
│                           │   ├── ClickUtil.java
│                           │   ├── DensityUtils.java
│                           │   ├── DownloadUtil.java
│                           │   ├── DpUtil.java
│                           │   ├── MhDataManager.java
│                           │   ├── PreferencesUtil.java
│                           │   ├── SPUtil.java
│                           │   ├── ScreenDimenUtil.java
│                           │   ├── SimpleDataManager.java
│                           │   ├── StringUtil.java
│                           │   ├── ToastUtil.java
│                           │   └── WordUtil.java
│                           └── views
│                               ├── AbsMhChildViewHolder.java
│                               ├── BeautyViewHolder.java
│                               ├── MhHaHaViewHolder.java
│                               ├── MhMainViewHolder.java
│                               ├── MhMakeupViewHolder.java
│                               ├── MhMeiYanBeautyViewHolder.java
│                               ├── MhMeiYanChildViewHolder.java
│                               ├── MhMeiYanFilterViewHolder.java
│                               ├── MhMeiYanOneKeyViewHolder.java
│                               ├── MhMeiYanShapeViewHolder.java
│                               ├── MhMeiYanViewHolder.java
│                               ├── MhTeXiaoActionViewHolder.java
│                               ├── MhTeXiaoChildViewHolder.java
│                               ├── MhTeXiaoHaHaViewHolder.java
│                               ├── MhTeXiaoSpecialViewHolder.java
│                               ├── MhTeXiaoViewHolder.java
│                               ├── MhTeXiaoWaterViewHolder.java
│                               ├── MhTieZhiChildViewHolder.java
│                               ├── MhTieZhiViewHolder.java
│                               └── SimpleBeautyViewHolder.java
├── chatroom
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           └── java
│               └── com
│                   └── yunbao
│                       └── chatroom
│                           ├── adapter
│                           │   ├── BaseLiveAnthorAdapter.java
│                           │   ├── GiftUserAdapter.java
│                           │   ├── LiveAdapter.java
│                           │   ├── LiveAnthorAdapter.java
│                           │   ├── LiveBgAdapter.java
│                           │   ├── LiveBillboardAdapter.java
│                           │   ├── LiveChatAdapter.java
│                           │   ├── LiveFriendAnthorAdapter.java
│                           │   ├── LiveGossipAnthorAdapter.java
│                           │   ├── LiveOnLineAdapter.java
│                           │   ├── LiveSongAnthorAdapter.java
│                           │   ├── LiveUpAdapter.java
│                           │   ├── UpWheatApplyAdapter.java
│                           │   └── WheatManangerAdapter.java
│                           ├── bean
│                           │   ├── ApplyHostInfo.java
│                           │   ├── ApplyResult.java
│                           │   ├── GiftUser.java
│                           │   ├── HeartSelectBean.java
│                           │   ├── ListBean.java
│                           │   ├── LiveBannerBean.java
│                           │   ├── LiveEndResultBean.java
│                           │   ├── LiveOrderCommitBean.java
│                           │   ├── LiveSetInfo.java
│                           │   ├── LiveTypeBean.java
│                           │   ├── LiveUserBean.java
│                           │   ├── MakePairBean.java
│                           │   ├── OpenLiveCommitBean.java
│                           │   ├── SkillPriceBean.java
│                           │   ├── SocketReceiveBean.java
│                           │   └── SocketSendBean.java
│                           ├── business
│                           │   ├── LiveType.java
│                           │   ├── behavior
│                           │   │   ├── ApplyQuequeBehavior.java
│                           │   │   ├── ApplyResultBehavior.java
│                           │   │   ├── BaseBehavior.java
│                           │   │   ├── CancleQueBehavior.java
│                           │   │   ├── EndLiveBehavior.java
│                           │   │   ├── GetApplyListBehavior.java
│                           │   │   ├── OpenCloseDialogBehavior.java
│                           │   │   ├── SkPowerBehavior.java
│                           │   │   ├── StopLiveBehavior.java
│                           │   │   ├── WatchApplyBehavior.java
│                           │   │   ├── factory
│                           │   │   │   ├── AbsBehaviorFactory.java
│                           │   │   │   ├── CacheBehaviorFactory.java
│                           │   │   │   ├── DisPatchBehaviorFactory.java
│                           │   │   │   ├── FriendBehaviorFactory.java
│                           │   │   │   ├── GossipBehaviorFactory.java
│                           │   │   │   └── SongBehaviorFactory.java
│                           │   │   ├── friend
│                           │   │   │   ├── FdApplyQuequeBehavior.java
│                           │   │   │   ├── FdApplyResultBehavior.java
│                           │   │   │   ├── FdCancleQueBeHavior.java
│                           │   │   │   ├── FdGetApplyListBehavior.java
│                           │   │   │   ├── FdSkPowerBehavior.java
│                           │   │   │   └── FdWatchApplyBehavior.java
│                           │   │   ├── gossip
│                           │   │   │   ├── GpApplyQuequeBehavior.java
│                           │   │   │   ├── GpApplyResultBehavior.java
│                           │   │   │   ├── GpCancleQueBeHavior.java
│                           │   │   │   ├── GpGetApplyListBehavior.java
│                           │   │   │   ├── GpSkPowerBehavior.java
│                           │   │   │   └── GpWatchApplyBehavior.java
│                           │   │   ├── song
│                           │   │   │   ├── SongApplyQuequeBehavior.java
│                           │   │   │   ├── SongApplyResultBehavior.java
│                           │   │   │   ├── SongCancleQueBeHavior.java
│                           │   │   │   ├── SongGetApplyListBehavior.java
│                           │   │   │   ├── SongSkPowerBehavior.java
│                           │   │   │   └── SongWatchApplyBehavior.java
│                           │   │   └── spatch
│                           │   │       ├── SpApplyQuequeBehavior.java
│                           │   │       ├── SpApplyResultBehavior.java
│                           │   │       ├── SpCancleQueBeHavior.java
│                           │   │       ├── SpGetApplyListBehavior.java
│                           │   │       ├── SpSkPowerBehavior.java
│                           │   │       └── SpWatchApplyBehavior.java
│                           │   ├── live
│                           │   │   ├── LiveActivityLifeModel.java
│                           │   │   ├── LiveState.java
│                           │   │   ├── presenter
│                           │   │   │   ├── ILivePresenter.java
│                           │   │   │   └── LivePresenter.java
│                           │   │   └── view
│                           │   │       ├── AudioListner.java
│                           │   │       └── ILiveView.java
│                           │   ├── socket
│                           │   │   ├── ILiveSocket.java
│                           │   │   ├── SocketIOImpl.java
│                           │   │   ├── SocketProxy.java
│                           │   │   ├── SuccessListner.java
│                           │   │   ├── base
│                           │   │   │   ├── BaseSocketMessageLisnerImpl.java
│                           │   │   │   ├── callback
│                           │   │   │   │   ├── BaseSocketMessageListner.java
│                           │   │   │   │   ├── ChatMessageListner.java
│                           │   │   │   │   ├── GiftMessageListner.java
│                           │   │   │   │   ├── SocketStateListner.java
│                           │   │   │   │   ├── SystemMessageListnter.java
│                           │   │   │   │   └── WheatControllListner.java
│                           │   │   │   └── mannger
│                           │   │   │       ├── ChatMannger.java
│                           │   │   │       ├── GiftMannger.java
│                           │   │   │       ├── SocketManager.java
│                           │   │   │       ├── SystemMessageMannger.java
│                           │   │   │       └── WheatControllerMannger.java
│                           │   │   ├── dispatch
│                           │   │   │   ├── DispatchSocketProxy.java
│                           │   │   │   ├── callback
│                           │   │   │   │   ├── OrderMessageListner.java
│                           │   │   │   │   ├── WheatLisnter.java
│                           │   │   │   │   └── WheeledWheatListner.java
│                           │   │   │   ├── impl
│                           │   │   │   │   └── DispatchSmsListnerImpl.java
│                           │   │   │   └── mannger
│                           │   │   │       ├── OrderMessageMannger.java
│                           │   │   │       ├── WheatMannger.java
│                           │   │   │       └── WheeledWheatMannger.java
│                           │   │   ├── friend
│                           │   │   │   ├── FriendSocketProxy.java
│                           │   │   │   ├── callback
│                           │   │   │   │   └── FriendStateListner.java
│                           │   │   │   ├── impl
│                           │   │   │   │   └── FriendSmsListnerImpl.java
│                           │   │   │   └── mannger
│                           │   │   │       ├── FriendStateMannger.java
│                           │   │   │       └── FriendWheatMannger.java
│                           │   │   ├── gossip
│                           │   │   │   ├── GossipSocketProxy.java
│                           │   │   │   ├── callback
│                           │   │   │   │   └── GossipWheatLisnter.java
│                           │   │   │   ├── impl
│                           │   │   │   │   └── GossipSmsListnerImpl.java
│                           │   │   │   └── mannger
│                           │   │   │       └── GossipWheatMannger.java
│                           │   │   └── song
│                           │   │       ├── SongSocketProxy.java
│                           │   │       ├── callback
│                           │   │       │   └── SongWheatListner.java
│                           │   │       ├── impl
│                           │   │       │   └── SongSmsListnerImpl.java
│                           │   │       └── mannger
│                           │   │           └── SongWheatMannger.java
│                           │   └── state
│                           │       ├── Stater.java
│                           │       └── audience
│                           │           ├── NormalState.java
│                           │           ├── NotQualifiedState.java
│                           │           ├── SpeakInTurnState.java
│                           │           ├── State.java
│                           │           └── UpperWheatState.java
│                           ├── event
│                           │   ├── AudioChangeEvent.java
│                           │   └── LiveChatRoomBossPlaceOrderEvent.java
│                           ├── http
│                           │   └── ChatRoomHttpUtil.java
│                           ├── ui
│                           │   ├── activity
│                           │   │   ├── LiveActivity.java
│                           │   │   ├── LivePieHallActivity.java
│                           │   │   ├── OpenLiveActivity.java
│                           │   │   ├── apply
│                           │   │   │   └── ApplyHostActivity.java
│                           │   │   ├── dispatch
│                           │   │   │   ├── LiveDispatchAudienceActivity.java
│                           │   │   │   ├── LiveDispatchHostActivity.java
│                           │   │   │   └── LiveSpatchActivity.java
│                           │   │   ├── friend
│                           │   │   │   ├── LiveFriendActivity.java
│                           │   │   │   ├── LiveFriendAudienceActivity.java
│                           │   │   │   └── LiveFriendHostActivity.java
│                           │   │   ├── gossip
│                           │   │   │   ├── LiveGossipActivity.java
│                           │   │   │   ├── LiveGossipAudienceActivity.java
│                           │   │   │   └── LiveGossipHostActivity.java
│                           │   │   └── song
│                           │   │       ├── LiveSongActivity.java
│                           │   │       ├── LiveSongAudienceActivity.java
│                           │   │       └── LiveSongHostActivity.java
│                           │   ├── dialog
│                           │   │   ├── AbsViewPagerDialogFragment.java
│                           │   │   ├── ApplySingerResultDialogFragment.java
│                           │   │   ├── FriendApplyListFragment.java
│                           │   │   ├── LineUpDialogFragment.java
│                           │   │   ├── LiveBillboardDialogFragment.java
│                           │   │   ├── LiveChatListDialogFragment.java
│                           │   │   ├── LiveChatRoomDialogFragment.java
│                           │   │   ├── LiveGiftDialogFragment.java
│                           │   │   ├── LiveInputDialogFragment.java
│                           │   │   ├── LiveOrderDialog.java
│                           │   │   ├── LiveRoomDialogFragment.java
│                           │   │   ├── LiveShareDialogFragment.java
│                           │   │   ├── LiveUserDialogFragment.java
│                           │   │   ├── LiveUserDialogFragment2.java
│                           │   │   ├── MakePairDialogFragment.java
│                           │   │   ├── MakePairFailureDialogFragment.java
│                           │   │   ├── UpWheatApplyDialogFragment.java
│                           │   │   ├── UpperWheatDialogFragment.java
│                           │   │   └── WheatManangerDialogFragment.java
│                           │   └── view
│                           │       ├── AbsLivePageViewHolder.java
│                           │       ├── ApplyManngerViewHolder.java
│                           │       ├── LauncherAdViewHolder.java
│                           │       ├── LiveAudienceBottomViewHolder.java
│                           │       ├── LiveBillboardViewHolder.java
│                           │       ├── LiveEndViewHolder.java
│                           │       ├── LiveOnLineViewHolder.java
│                           │       ├── LiveRoomDetailViewHolder.java
│                           │       ├── LiveTabulationViewHolder.java
│                           │       ├── SpeakTurnViewHolder.java
│                           │       ├── WheatManangerViewHolder.java
│                           │       ├── apply
│                           │       │   ├── AbsApplyHostViewHolder.java
│                           │       │   ├── ApplyHostResultViewHolder.java
│                           │       │   ├── ApplyHostViewHolder.java
│                           │       │   └── NoApplyHostViewHolder.java
│                           │       ├── bottom
│                           │       │   ├── LiveDisPatchHostViewHolder.java
│                           │       │   ├── LiveFriendHostViewHolder.java
│                           │       │   └── LiveHostBottomViewHolder.java
│                           │       └── seat
│                           │           ├── AbsLiveSeatViewHolder.java
│                           │           ├── LiveFriendSeatViewHolder.java
│                           │           ├── LiveGossipSeatViewHolder.java
│                           │           ├── LiveSongSeatViewHolder.java
│                           │           └── LiveSpatchViewHolder.java
│                           └── widet
│                               ├── BubbleLayout.java
│                               ├── CustomPopupWindow.java
│                               └── StateView.java
├── common
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           └── java
│               └── com
│                   └── yunbao
│                       └── common
│                           ├── CommonAppConfig.java
│                           ├── CommonAppContext.java
│                           ├── Constants.java
│                           ├── HtmlConfig.java
│                           ├── activity
│                           │   ├── AbsActivity.java
│                           │   ├── ErrorActivity.java
│                           │   └── WebViewActivity.java
│                           ├── adapter
│                           │   ├── CommonShareAdapter.java
│                           │   ├── ImChatFaceAdapter.java
│                           │   ├── ImChatFacePagerAdapter.java
│                           │   ├── RefreshAdapter.java
│                           │   ├── ViewPagerAdapter.java
│                           │   ├── base
│                           │   │   ├── BaseMutiRecyclerAdapter.java
│                           │   │   ├── BaseReclyViewHolder.java
│                           │   │   └── BaseRecyclerAdapter.java
│                           │   └── radio
│                           │       ├── CheckEntity.java
│                           │       ├── IRadioChecker.java
│                           │       └── RadioAdapter.java
│                           ├── bean
│                           │   ├── AdBean.java
│                           │   ├── ChargeSuccessBean.java
│                           │   ├── ChatAnchorParam.java
│                           │   ├── ChatAudienceParam.java
│                           │   ├── ChatGiftBean.java
│                           │   ├── ChatReceiveGiftBean.java
│                           │   ├── CoinBean.java
│                           │   ├── CoinPayBean.java
│                           │   ├── ConditionLevel.java
│                           │   ├── ConfigBean.java
│                           │   ├── DataListner.java
│                           │   ├── ExportNamer.java
│                           │   ├── FansUserBean.java
│                           │   ├── GoodsBean.java
│                           │   ├── LevelBean.java
│                           │   ├── LiveAnthorBean.java
│                           │   ├── LiveBean.java
│                           │   ├── LiveChatBean.java
│                           │   ├── LiveClassBean.java
│                           │   ├── LiveGiftBean.java
│                           │   ├── LiveInfo.java
│                           │   ├── OrderBean.java
│                           │   ├── OrderCommentBean.java
│                           │   ├── Reason.java
│                           │   ├── SkillBean.java
│                           │   ├── TxLocationBean.java
│                           │   ├── TxLocationPoiBean.java
│                           │   ├── UserBean.java
│                           │   ├── UserItemBean.java
│                           │   ├── UserItemBean2.java
│                           │   ├── XingZuoBean.java
│                           │   └── commit
│                           │       ├── BaseObservableField.java
│                           │       ├── CommitEntity.java
│                           │       ├── ObservableInteager.java
│                           │       ├── ObservableLong.java
│                           │       └── ObservableString.java
│                           ├── business
│                           │   ├── ConditionModel.java
│                           │   ├── TimeModel.java
│                           │   ├── acmannger
│                           │   │   ├── ActivityMannger.java
│                           │   │   └── ReleaseListner.java
│                           │   └── liveobsever
│                           │       ├── DataChangeListner.java
│                           │       ├── DataObsever.java
│                           │       ├── LifeObjectHolder.java
│                           │       ├── LifeObserver.java
│                           │       └── LifeVoiceMediaHelper.java
│                           ├── custom
│                           │   ├── AppBarLayoutBehavior.java
│                           │   ├── CancleSelfRadioButton.java
│                           │   ├── CheckImageView.java
│                           │   ├── CircleProgress.java
│                           │   ├── CoinGiveLayout.java
│                           │   ├── CommonRefreshView.java
│                           │   ├── DrawGiftView.java
│                           │   ├── DrawableCheckBox.java
│                           │   ├── DrawableRadioButton.java
│                           │   ├── DrawableRadioButton2.java
│                           │   ├── DrawableTextView.java
│                           │   ├── FixAppBarLayoutBehavior.java
│                           │   ├── FlowRadioDataGroup.java
│                           │   ├── FlowRadioGroup.java
│                           │   ├── InterceptFrameLayout.java
│                           │   ├── ItemDecoration.java
│                           │   ├── ItemLinearLayout.java
│                           │   ├── ItemSlideHelper.java
│                           │   ├── LadderTextView.java
│                           │   ├── MaxHeightRecyclerView.java
│                           │   ├── MyFrameLayout1.java
│                           │   ├── MyFrameLayout2.java
│                           │   ├── MyImageView2.java
│                           │   ├── MyLinearLayout1.java
│                           │   ├── MyLinearLayout2.java
│                           │   ├── MyLinearLayout3.java
│                           │   ├── MyLinearLayout4.java
│                           │   ├── MyLinearLayout5.java
│                           │   ├── MyLinearLayout6.java
│                           │   ├── MyPreDrawFilter.java
│                           │   ├── MyRadioButton.java
│                           │   ├── MyRelativeLayout1.java
│                           │   ├── MyRelativeLayout2.java
│                           │   ├── MyRelativeLayout5.java
│                           │   ├── MyRelativeLayout6.java
│                           │   ├── MyRelativeLayout7.java
│                           │   ├── MyViewPager.java
│                           │   ├── RatingBar.java
│                           │   ├── RatioGifImageView.java
│                           │   ├── RatioImageView.java
│                           │   ├── RatioRoundImageView.java
│                           │   ├── ScaleTransitionPagerTitleView.java
│                           │   ├── ScrollSpeedLinearLayoutManger.java
│                           │   ├── ShadowContainer.java
│                           │   ├── SquareImageView.java
│                           │   ├── SquareRoundedImageView.java
│                           │   ├── TabButton.java
│                           │   ├── TabButtonGroup.java
│                           │   ├── UIFactory.java
│                           │   ├── ValueFrameAnimator.java
│                           │   ├── VerticalImageSpan.java
│                           │   ├── VerticalViewPager.java
│                           │   ├── ViewPagerSnapHelper.java
│                           │   ├── VoiceView.java
│                           │   ├── WarpLinearLayout.java
│                           │   ├── ZoomImageView.java
│                           │   ├── ZoomView.java
│                           │   ├── refresh
│                           │   │   └── RxRefreshView.java
│                           │   └── viewanimator
│                           │       ├── AnimationBuilder.java
│                           │       ├── AnimationListener.java
│                           │       └── ViewAnimator.java
│                           ├── dialog
│                           │   ├── AbsDialogFragment.java
│                           │   ├── BottomDealFragment.java
│                           │   ├── ChatFaceDialog.java
│                           │   ├── ChooseSexDialog.java
│                           │   ├── CommonShareDialogFragment.java
│                           │   ├── NotCancelableDialog.java
│                           │   ├── SelectDialogFragment.java
│                           │   └── ShareWithCopyDialogFragment.java
│                           ├── event
│                           │   ├── BlackEvent.java
│                           │   ├── CoinChangeEvent.java
│                           │   ├── FollowEvent.java
│                           │   ├── ImMsgEvent.java
│                           │   ├── LaunchStackEvent.java
│                           │   ├── LocationEvent.java
│                           │   ├── LoginInvalidEvent.java
│                           │   ├── LoginSuccessEvent.java
│                           │   ├── MatchSuccessEvent.java
│                           │   ├── OrderChangedEvent.java
│                           │   ├── OrderEvaluateCompleteEvent.java
│                           │   ├── OrderStatusEvent.java
│                           │   ├── ShowLiveRoomFloatEvent.java
│                           │   ├── ShowLiveRoomFloatWindowEvent.java
│                           │   ├── ShowOrHideLiveRoomFloatWindowEvent.java
│                           │   └── UpdateFieldEvent.java
│                           ├── fragment
│                           │   ├── ProcessFragment.java
│                           │   └── ProcessFragmentNew.java
│                           ├── glide
│                           │   └── ImgLoader.java
│                           ├── http
│                           │   ├── CommonHttpConsts.java
│                           │   ├── CommonHttpUtil.java
│                           │   ├── Data.java
│                           │   ├── HttpCallback.java
│                           │   ├── HttpClient.java
│                           │   ├── HttpLoggingInterceptor.java
│                           │   ├── HttpUploadClient.java
│                           │   └── JsonBean.java
│                           ├── interfaces
│                           │   ├── ActivityResultCallback.java
│                           │   ├── CommonCallback.java
│                           │   ├── ImageResultCallback.java
│                           │   ├── KeyBoardHeightChangeListener.java
│                           │   ├── LifeCycleListener.java
│                           │   ├── OnFaceClickListener.java
│                           │   ├── OnItemClickListener.java
│                           │   ├── PermissionCallback.java
│                           │   └── VideoResultCallback.java
│                           ├── mob
│                           │   ├── LoginData.java
│                           │   ├── MobBean.java
│                           │   ├── MobCallback.java
│                           │   ├── MobConst.java
│                           │   ├── MobLoginUtil.java
│                           │   ├── MobShareUtil.java
│                           │   └── ShareData.java
│                           ├── pay
│                           │   ├── PayCallback.java
│                           │   ├── PayPresenter.java
│                           │   ├── ali
│                           │   │   ├── AliPayBuilder.java
│                           │   │   ├── Base64.java
│                           │   │   └── SignUtils.java
│                           │   ├── google
│                           │   │   └── GooglePayTask.java
│                           │   ├── paypal
│                           │   │   └── PaypalPayTask.java
│                           │   └── wx
│                           │       ├── WxApiWrapper.java
│                           │       └── WxPayBuilder.java
│                           ├── presenter
│                           │   └── GiftAnimViewHolder.java
│                           ├── server
│                           │   ├── IRequestManager.java
│                           │   ├── MapBuilder.java
│                           │   ├── OkGoRequestMannger.java
│                           │   ├── RequestFactory.java
│                           │   ├── RxUtils.java
│                           │   ├── SeverConfig.java
│                           │   ├── annotation
│                           │   │   └── Text2.java
│                           │   ├── converter
│                           │   │   ├── Convert.java
│                           │   │   ├── FashJsonConvert.java
│                           │   │   ├── FastJsonConvert.java
│                           │   │   ├── IConvert.java
│                           │   │   └── JsonConvert.java
│                           │   ├── entity
│                           │   │   ├── BaseRequest.java
│                           │   │   ├── BaseResponse.java
│                           │   │   ├── Data.java
│                           │   │   ├── SimpleData.java
│                           │   │   └── SimpleResponse.java
│                           │   ├── generic
│                           │   │   └── ParameterizedTypeImpl.java
│                           │   ├── interceptor
│                           │   │   └── ResponseInterceptor.java
│                           │   └── observer
│                           │       ├── DefaultObserver.java
│                           │       ├── DialogObserver.java
│                           │       └── LockClickObserver.java
│                           ├── service
│                           │   └── VoipFloatService.java
│                           ├── upload
│                           │   ├── AWSTransferUtil.java
│                           │   ├── AWSUploadImpl.java
│                           │   ├── FileUploadManager.java
│                           │   ├── UploadBean.java
│                           │   ├── UploadCallback.java
│                           │   ├── UploadInfoBean.java
│                           │   ├── UploadQnImpl.java
│                           │   ├── UploadQnImpl2.java
│                           │   └── UploadStrategy.java
│                           ├── utils
│                           │   ├── ActivityResultUtil.java
│                           │   ├── BirthdayUtil.java
│                           │   ├── BitmapUtil.java
│                           │   ├── CleanLeakUtils.java
│                           │   ├── ClickUtil.java
│                           │   ├── CommonIconUtil.java
│                           │   ├── DateFormatUtil.java
│                           │   ├── DecryptUtil.java
│                           │   ├── DeviceUtils.java
│                           │   ├── DialogUitl.java
│                           │   ├── DownloadUtil.java
│                           │   ├── DpUtil.java
│                           │   ├── FaceTextUtil.java
│                           │   ├── FaceUtil.java
│                           │   ├── FieldUtil.java
│                           │   ├── FileUtil.java
│                           │   ├── FormatUtil.java
│                           │   ├── GifCacheUtil.java
│                           │   ├── GiftTextRender.java
│                           │   ├── GlideCatchUtil.java
│                           │   ├── HeybroadHelper.java
│                           │   ├── ImageUtil.java
│                           │   ├── JsonUtil.java
│                           │   ├── KeyBoardHeightUtil.java
│                           │   ├── KeyBoardHeightUtil2.java
│                           │   ├── KeyBoardUtil.java
│                           │   ├── L.java
│                           │   ├── LanguageUtil.java
│                           │   ├── ListUtil.java
│                           │   ├── LocationUtil.java
│                           │   ├── MD5Util.java
│                           │   ├── MediaUtil.java
│                           │   ├── MoneyHelper.java
│                           │   ├── Parser.java
│                           │   ├── PermissionUtil.java
│                           │   ├── ProcessImageUtil.java
│                           │   ├── ProcessResultUtil.java
│                           │   ├── RandomUtil.java
│                           │   ├── ResourceUtil.java
│                           │   ├── RomUtil.java
│                           │   ├── RouteUtil.java
│                           │   ├── ScreenDimenUtil.java
│                           │   ├── SpUtil.java
│                           │   ├── SpannableStringUtils.java
│                           │   ├── StringUtil.java
│                           │   ├── SystemUtil.java
│                           │   ├── ToastHigherUtil.java
│                           │   ├── ToastUtil.java
│                           │   ├── TxImCacheUtil.java
│                           │   ├── ValidatePhoneUtil.java
│                           │   ├── VersionUtil.java
│                           │   ├── VideoChooseBean.java
│                           │   ├── VideoLocalUtil.java
│                           │   ├── ViewUtil.java
│                           │   ├── VoiceMediaPlayerUtil.java
│                           │   ├── WordFilterUtil.java
│                           │   └── WordUtil.java
│                           └── views
│                               ├── AbsCommonViewHolder.java
│                               ├── AbsLivePageViewHolder.java
│                               ├── AbsMainHomeChildViewHolder.java
│                               ├── AbsMainViewHolder.java
│                               ├── AbsViewHolder.java
│                               ├── AbsViewHolder2.java
│                               └── LiveGiftViewHolder.java
├── dynamic
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           └── java
│               └── com
│                   └── yunbao
│                       └── dynamic
│                           ├── adapter
│                           │   ├── DynamicCommentAdapter.java
│                           │   ├── DynamicResourceAdapter.java
│                           │   ├── GalleryAdapter.java
│                           │   ├── MyDynamicAdapter.java
│                           │   ├── SelectPhotoAdapter.java
│                           │   └── SelectVideoAdapter.java
│                           ├── bean
│                           │   ├── CommitPubDynamicBean.java
│                           │   ├── DynamicBean.java
│                           │   ├── DynamicCommentBean.java
│                           │   ├── DynamicSkillBean.java
│                           │   ├── DynamicUserBean.java
│                           │   ├── MyDynamicBean.java
│                           │   ├── ResourseBean.java
│                           │   └── UserLabelInfoBean.java
│                           ├── business
│                           │   ├── AnimHelper.java
│                           │   └── DynamicUIFactory.java
│                           ├── event
│                           │   ├── DynamicCommentEvent.java
│                           │   ├── DynamicCommentNumberEvent.java
│                           │   └── DynamicLikeEvent.java
│                           ├── http
│                           │   └── DynamicHttpUtil.java
│                           ├── ui
│                           │   ├── activity
│                           │   │   ├── DynamicDetailActivity.java
│                           │   │   ├── DynamicReportActivity.java
│                           │   │   ├── DynamicVideoActivity.java
│                           │   │   ├── GalleryActivity.java
│                           │   │   ├── MyDynamicActivity.java
│                           │   │   ├── SelectPhotoActivity.java
│                           │   │   └── SelectVideoActivity.java
│                           │   ├── dialog
│                           │   │   ├── CommentDialogFragment.java
│                           │   │   ├── DynamicInputDialogFragment.java
│                           │   │   └── VoiceRecordDialogFragment.java
│                           │   └── view
│                           │       ├── AbsDynamicDetailViewHolder.java
│                           │       ├── DynamicCommentViewHolder.java
│                           │       ├── GalleryViewHolder.java
│                           │       ├── MysDynamicVIewHolder.java
│                           │       ├── NormalDynamicViewHolder.java
│                           │       ├── NormalScrollDynamicViewHolder.java
│                           │       ├── PhotoDynamiceViewHolder.java
│                           │       ├── VideoDynamiceViewHolder.java
│                           │       └── VoiceDynamiceViewHolderScroll.java
│                           ├── util
│                           │   ├── AudioRecorderEx.java
│                           │   ├── CommentTextRender.java
│                           │   └── FilePathUtil.java
│                           └── widet
│                               ├── SimulateReclyViewTouchLisnter.java
│                               ├── StartChangeOffectListner.java
│                               └── VoicePlayView.java
├── gradle.properties
├── gradlew
├── gradlew.bat
├── im
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           └── java
│               └── com
│                   └── yunbao
│                       └── im
│                           ├── activity
│                           │   ├── ChatActivity.java
│                           │   ├── ChatChooseImageActivity.java
│                           │   ├── ChatRoomActivity.java
│                           │   ├── LocationActivity.java
│                           │   ├── MediaCallActivity.java
│                           │   ├── SystemMainActivity.java
│                           │   └── SystemMessageActivity.java
│                           ├── adapter
│                           │   ├── ChatGiftAdapter.java
│                           │   ├── ChatGiftCountAdapter.java
│                           │   ├── ChatGiftPagerAdapter.java
│                           │   ├── ChatImagePreviewAdapter.java
│                           │   ├── ImChatChooseImageAdapter.java
│                           │   ├── ImListAdapter.java
│                           │   ├── ImRoomAdapter.java
│                           │   ├── LocationAdapter.java
│                           │   ├── SysMainAdapter.java
│                           │   └── SystemMessageAdapter.java
│                           ├── bean
│                           │   ├── ChatChooseImageBean.java
│                           │   ├── ChatInfoBean.java
│                           │   ├── IMLiveBean.java
│                           │   ├── ImChatImageBean.java
│                           │   ├── ImMessageBean.java
│                           │   ├── ImMsgLocationBean.java
│                           │   ├── ImUserBean.java
│                           │   ├── OrderTipBean.java
│                           │   ├── SystemMessageBean.java
│                           │   ├── TestBean.java
│                           │   └── TimeInfo.java
│                           ├── business
│                           │   ├── CallIMHelper.java
│                           │   ├── CallLivingState.java
│                           │   ├── ICallPresnter.java
│                           │   ├── IExpotFlowContainer.java
│                           │   ├── IRoom.java
│                           │   ├── IVideoCallView.java
│                           │   ├── SysPermisssonFragment.java
│                           │   ├── TimeModel.java
│                           │   ├── VideoCallPresneter.java
│                           │   ├── WindowAddHelper.java
│                           │   └── state
│                           │       ├── CallStateMachine.java
│                           │       ├── FreeCallState.java
│                           │       ├── ICallState.java
│                           │       └── WaitState.java
│                           ├── config
│                           │   ├── CallConfig.java
│                           │   └── GenerateTestUserSig.java
│                           ├── custom
│                           │   ├── BubbleLayout.java
│                           │   ├── CallButtonLayout.java
│                           │   ├── ChatVoiceLayout.java
│                           │   ├── FloatFrameLayout.java
│                           │   ├── FlowVideoLayout.java
│                           │   ├── GiftPageViewPager.java
│                           │   ├── MyImageView.java
│                           │   ├── MyRelativeLayout.java
│                           │   └── PopupLayout.java
│                           ├── dialog
│                           │   ├── ChatGiftDialogFragment.java
│                           │   ├── ChatImageDialog.java
│                           │   ├── ChatImageDialog2.java
│                           │   ├── ChatMoreDialog.java
│                           │   ├── ChatOptionDialogFragment.java
│                           │   └── SystemMessageDialogFragment.java
│                           ├── event
│                           │   ├── AcceptCallEvent.java
│                           │   ├── CallBusyEvent.java
│                           │   ├── CancleCallEvent.java
│                           │   ├── ChatLiveImEvent.java
│                           │   ├── DripEvent.java
│                           │   ├── ImLoginEvent.java
│                           │   ├── ImOffLineMsgEvent.java
│                           │   ├── ImRemoveAllMsgEvent.java
│                           │   ├── ImRoamMsgEvent.java
│                           │   ├── ImUnReadCountEvent.java
│                           │   ├── ImUserMsgEvent.java
│                           │   ├── NowServiceEvent.java
│                           │   ├── RefuseCallEvent.java
│                           │   ├── SystemMsgEvent.java
│                           │   └── VideoAllCloseEvent.java
│                           ├── http
│                           │   ├── ImHttpConsts.java
│                           │   └── ImHttpUtil.java
│                           ├── interfaces
│                           │   ├── ChatRoomActionListener.java
│                           │   ├── ImClient.java
│                           │   └── SendMsgResultCallback.java
│                           ├── receiver
│                           │   └── HomeWatcherReceiver.java
│                           ├── service
│                           │   └── CallService.java
│                           ├── utils
│                           │   ├── ImDateUtil.java
│                           │   ├── ImMessageUtil.java
│                           │   ├── ImTextRender.java
│                           │   ├── ImageUtil.java
│                           │   ├── MediaRecordUtil.java
│                           │   ├── TxImMessageUtil.java
│                           │   ├── Utils.java
│                           │   └── VoiceMediaPlayerUtil.java
│                           └── views
│                               ├── ChatListViewHolder.java
│                               ├── ChatRoomDialogViewHolder.java
│                               ├── ChatRoomViewHolder.java
│                               ├── EditVoiceViewHolder.java
│                               ├── OrderMessageViewHolder.java
│                               ├── SystemMessageViewHolder.java
│                               └── call
│                                   ├── AbsCallViewHolder.java
│                                   ├── AudioCallViewHolder.java
│                                   ├── CallWaitViewHolder.java
│                                   └── VideoCallViewHolder.java
├── live
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           └── java
│               └── com
│                   └── yunbao
│                       └── live
│                           ├── LiveConfig.java
│                           ├── activity
│                           │   ├── LiveActivity.java
│                           │   ├── LiveAddImpressActivity.java
│                           │   ├── LiveAdminListActivity.java
│                           │   ├── LiveAnchorActivity.java
│                           │   ├── LiveAudienceActivity.java
│                           │   ├── LiveBlackActivity.java
│                           │   ├── LiveChooseClassActivity.java
│                           │   ├── LiveContributeActivity.java
│                           │   ├── LiveGoodsAddActivity.java
│                           │   ├── LiveGuardListActivity.java
│                           │   ├── LiveRecordActivity.java
│                           │   ├── LiveRecordPlayActivity.java
│                           │   ├── LiveReportActivity.java
│                           │   ├── LiveShutUpActivity.java
│                           │   ├── RoomManageActivity.java
│                           │   └── RoomManageDetailActivity.java
│                           ├── adapter
│                           │   ├── DailyTaskAdapter.java
│                           │   ├── GuardAdapter.java
│                           │   ├── GuardRightAdapter.java
│                           │   ├── LiveAdminListAdapter.java
│                           │   ├── LiveAdminRoomAdapter.java
│                           │   ├── LiveBlackAdapter.java
│                           │   ├── LiveChatAdapter.java
│                           │   ├── LiveFunctionAdapter.java
│                           │   ├── LiveGiftAdapter.java
│                           │   ├── LiveGiftCountAdapter.java
│                           │   ├── LiveGiftPagerAdapter.java
│                           │   ├── LiveGoodsAdapter.java
│                           │   ├── LiveGoodsAddAdapter.java
│                           │   ├── LivePkAdapter.java
│                           │   ├── LivePlatGoodsAddAdapter.java
│                           │   ├── LiveReadyClassAdapter.java
│                           │   ├── LiveReadyShareAdapter.java
│                           │   ├── LiveRecordAdapter.java
│                           │   ├── LiveReportAdapter.java
│                           │   ├── LiveRoomScrollAdapter.java
│                           │   ├── LiveRoomTypeAdapter.java
│                           │   ├── LiveShareAdapter.java
│                           │   ├── LiveShopAdapter.java
│                           │   ├── LiveShutUpAdapter.java
│                           │   ├── LiveTimeChargeAdapter.java
│                           │   ├── LiveUserAdapter.java
│                           │   ├── LiveVoiceApplyUpAdapter.java
│                           │   ├── LiveVoiceControlAdapter.java
│                           │   ├── LiveVoiceFaceAdapter.java
│                           │   ├── LiveVoiceGiftAdapter.java
│                           │   ├── LiveVoiceLinkMicAdapter.java
│                           │   ├── LuckPanRecordAdapter.java
│                           │   ├── LuckPanWinAdapter.java
│                           │   ├── RedPackAdapter.java
│                           │   └── RedPackResultAdapter.java
│                           ├── bean
│                           │   ├── BackPackGiftBean.java
│                           │   ├── DailyTaskBean.java
│                           │   ├── GlobalGiftBean.java
│                           │   ├── GuardBuyBean.java
│                           │   ├── GuardRightBean.java
│                           │   ├── GuardUserBean.java
│                           │   ├── ImpressBean.java
│                           │   ├── LiveAdminRoomBean.java
│                           │   ├── LiveBeanReal.java
│                           │   ├── LiveBuyGuardMsgBean.java
│                           │   ├── LiveChatBean.java
│                           │   ├── LiveConfigBean.java
│                           │   ├── LiveDanMuBean.java
│                           │   ├── LiveEnterRoomBean.java
│                           │   ├── LiveFunctionBean.java
│                           │   ├── LiveGiftPrizePoolWinBean.java
│                           │   ├── LiveGuardInfo.java
│                           │   ├── LiveLuckGiftWinBean.java
│                           │   ├── LivePkBean.java
│                           │   ├── LiveReceiveGiftBean.java
│                           │   ├── LiveRecordBean.java
│                           │   ├── LiveReportBean.java
│                           │   ├── LiveRoomTypeBean.java
│                           │   ├── LiveShutUpBean.java
│                           │   ├── LiveStickerBean.java
│                           │   ├── LiveTimeChargeBean.java
│                           │   ├── LiveUserGiftBean.java
│                           │   ├── LiveVoiceControlBean.java
│                           │   ├── LiveVoiceFaceBean.java
│                           │   ├── LiveVoiceGiftBean.java
│                           │   ├── LiveVoiceLinkMicBean.java
│                           │   ├── LuckPanBean.java
│                           │   ├── RedPackBean.java
│                           │   ├── RedPackResultBean.java
│                           │   ├── SearchUserBean.java
│                           │   ├── TiFilter.java
│                           │   ├── TurntableConfigBean.java
│                           │   ├── TurntableGiftBean.java
│                           │   └── VoiceRoomAccPullBean.java
│                           ├── custom
│                           │   ├── FrameImageView.java
│                           │   ├── GiftMarkView.java
│                           │   ├── GiftPageViewPager.java
│                           │   ├── LiveAudienceRecyclerView.java
│                           │   ├── LiveLightView.java
│                           │   ├── MusicProgressTextView.java
│                           │   ├── MyFrameLayout3.java
│                           │   ├── MyFrameLayout4.java
│                           │   ├── MyImageView.java
│                           │   ├── MyRelativeLayout1.java
│                           │   ├── MyTextView.java
│                           │   ├── MyTextView2.java
│                           │   ├── PkProgressBar.java
│                           │   ├── ProgressTextView.java
│                           │   ├── StarView.java
│                           │   └── TopGradual.java
│                           ├── dialog
│                           │   ├── DailyTaskDialogFragment.java
│                           │   ├── GiftPrizePoolFragment.java
│                           │   ├── LiveFunctionDialogFragment.java
│                           │   ├── LiveGiftDialogFragment.java
│                           │   ├── LiveGoodsDialogFragment.java
│                           │   ├── LiveGuardBuyDialogFragment.java
│                           │   ├── LiveGuardDialogFragment.java
│                           │   ├── LiveInputDialogFragment.java
│                           │   ├── LiveLinkMicListDialogFragment.java
│                           │   ├── LiveLinkMicPkSearchDialog.java
│                           │   ├── LiveRedPackListDialogFragment.java
│                           │   ├── LiveRedPackResultDialogFragment.java
│                           │   ├── LiveRedPackRobDialogFragment.java
│                           │   ├── LiveRedPackSendDialogFragment.java
│                           │   ├── LiveRoomCheckDialogFragment.java
│                           │   ├── LiveRoomCheckDialogFragment2.java
│                           │   ├── LiveRoomTypeDialogFragment.java
│                           │   ├── LiveShareDialogFragment.java
│                           │   ├── LiveShopDialogFragment.java
│                           │   ├── LiveTimeDialogFragment.java
│                           │   ├── LiveVoiceApplyUpFragment.java
│                           │   ├── LiveVoiceControlFragment.java
│                           │   ├── LiveVoiceFaceFragment.java
│                           │   ├── LuckPanDialogFragment.java
│                           │   ├── LuckPanRecordDialogFragment.java
│                           │   ├── LuckPanTipDialogFragment.java
│                           │   └── LuckPanWinDialogFragment.java
│                           ├── event
│                           │   ├── LinkMicTxAccEvent.java
│                           │   ├── LinkMicTxMixStreamEvent.java
│                           │   ├── LiveAudienceVoiceExitEvent.java
│                           │   ├── LiveAudienceVoiceOpenEvent.java
│                           │   ├── LiveRoomChangeEvent.java
│                           │   └── LiveVoiceMicStatusEvent.java
│                           ├── floatwindow
│                           │   ├── FloatPermissionActivity.java
│                           │   ├── FloatWindow.java
│                           │   ├── FloatWindowPermission.java
│                           │   ├── FloatWindowUtil.java
│                           │   ├── Miui.java
│                           │   └── Rom.java
│                           ├── http
│                           │   ├── LiveHttpConsts.java
│                           │   ├── LiveHttpUtil.java
│                           │   └── MusicUrlCallback.java
│                           ├── interfaces
│                           │   ├── ILiveLinkMicViewHolder.java
│                           │   ├── ILivePushViewHolder.java
│                           │   ├── LiveFunctionClickListener.java
│                           │   ├── LivePushListener.java
│                           │   └── RedPackCountDownListener.java
│                           ├── music
│                           │   ├── LiveMusicAdapter.java
│                           │   ├── LiveMusicBean.java
│                           │   ├── LiveMusicDialogFragment.java
│                           │   ├── LiveMusicPlayer.java
│                           │   ├── LrcBean.java
│                           │   ├── LrcParser.java
│                           │   ├── LrcTextView.java
│                           │   └── db
│                           │       ├── MusicDbHelper.java
│                           │       └── MusicDbManager.java
│                           ├── presenter
│                           │   ├── LiveDanmuPresenter.java
│                           │   ├── LiveEnterRoomAnimPresenter.java
│                           │   ├── LiveGiftAnimPresenter.java
│                           │   ├── LiveLightAnimPresenter.java
│                           │   ├── LiveLinkMicAnchorPresenter.java
│                           │   ├── LiveLinkMicPkPresenter.java
│                           │   ├── LiveLinkMicPresenter.java
│                           │   ├── LiveRoomCheckLivePresenter.java
│                           │   ├── LiveRoomCheckLivePresenter2.java
│                           │   └── UserHomeSharePresenter.java
│                           ├── socket
│                           │   ├── GameActionListenerImpl.java
│                           │   ├── SocketChatUtil.java
│                           │   ├── SocketClient.java
│                           │   ├── SocketLinkMicAnchorUtil.java
│                           │   ├── SocketLinkMicPkUtil.java
│                           │   ├── SocketLinkMicUtil.java
│                           │   ├── SocketMessageListener.java
│                           │   ├── SocketReceiveBean.java
│                           │   ├── SocketSendBean.java
│                           │   └── SocketVoiceRoomUtil.java
│                           ├── utils
│                           │   ├── KsyMhFilter.java
│                           │   ├── LiveIconUtil.java
│                           │   ├── LiveStorge.java
│                           │   ├── LiveTextRender.java
│                           │   └── LogUtil.java
│                           └── views
│                               ├── AbsLiveGiftViewHolder.java
│                               ├── AbsLiveLinkMicPlayViewHolder.java
│                               ├── AbsLiveLinkMicPushViewHolder.java
│                               ├── AbsLivePushViewHolder.java
│                               ├── AbsLiveViewHolder.java
│                               ├── AbsUserHomeViewHolder.java
│                               ├── DanmuViewHolder.java
│                               ├── LauncherAdViewHolder.java
│                               ├── LiveAddImpressViewHolder.java
│                               ├── LiveAdminListViewHolder.java
│                               ├── LiveAnchorViewHolder.java
│                               ├── LiveAudienceViewHolder.java
│                               ├── LiveContributeViewHolder.java
│                               ├── LiveEndViewHolder.java
│                               ├── LiveGiftDaoViewHolder.java
│                               ├── LiveGiftDrawViewHolder.java
│                               ├── LiveGiftGiftViewHolder.java
│                               ├── LiveGiftLuckTopViewHolder.java
│                               ├── LiveGiftPackageViewHolder.java
│                               ├── LiveGiftPrizePoolViewHolder.java
│                               ├── LiveGiftViewHolder.java
│                               ├── LiveGoodsAddViewHolder.java
│                               ├── LiveLinkMicPkViewHolder.java
│                               ├── LiveLinkMicPlayKsyViewHolder.java
│                               ├── LiveLinkMicPlayTxViewHolder.java
│                               ├── LiveLinkMicPushKsyViewHolder.java
│                               ├── LiveLinkMicPushTxViewHolder.java
│                               ├── LiveMusicViewHolder.java
│                               ├── LiveMyLiveRoomViewHolder.java
│                               ├── LiveMyRoomViewHolder.java
│                               ├── LivePlatGoodsAddViewHolder.java
│                               ├── LivePlayKsyViewHolder.java
│                               ├── LivePlayTxViewHolder.java
│                               ├── LivePushKsyViewHolder.java
│                               ├── LivePushTxViewHolder.java
│                               ├── LiveReadyViewHolder.java
│                               ├── LiveRecordPlayViewHolder.java
│                               ├── LiveRecordViewHolder.java
│                               ├── LiveRoomBtnViewHolder.java
│                               ├── LiveRoomPlayViewHolder.java
│                               ├── LiveRoomViewHolder.java
│                               ├── LiveTitleAnimViewHolder.java
│                               ├── LiveVoiceAnchorViewHolder.java
│                               ├── LiveVoiceAudienceViewHolder.java
│                               ├── LiveVoiceLinkMicViewHolder.java
│                               ├── LiveVoicePlayTxViewHolder.java
│                               ├── LiveVoicePlayUtil.java
│                               ├── LiveVoicePushTxViewHolder.java
│                               ├── LiveWebViewHolder.java
│                               └── LuckLiveGiftViewHolder.java
├── local.properties
├── main
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           └── java
│               └── com
│                   └── yunbao
│                       └── main
│                           ├── activity
│                           │   ├── AlbumGalleryActivity.java
│                           │   ├── AllClassActivity.java
│                           │   ├── AllSkillActivity.java
│                           │   ├── BlackListActivity.java
│                           │   ├── CashActivity.java
│                           │   ├── ChatRoomMoreListActivity.java
│                           │   ├── ChooseGreatActivity.java
│                           │   ├── ChoosePhoneCountryCodeActivity.java
│                           │   ├── ChooseSkillActivity.java
│                           │   ├── DidiOrderActivity.java
│                           │   ├── DripOrderActivity.java
│                           │   ├── EditAddrActivity.java
│                           │   ├── EditInterestActivity.java
│                           │   ├── EditJobActivity.java
│                           │   ├── EditNameActivity.java
│                           │   ├── EditProfileActivity.java
│                           │   ├── EditSchoolActivity.java
│                           │   ├── EditSignActivity.java
│                           │   ├── EditSkillActivity.java
│                           │   ├── EditVoiceActivity.java
│                           │   ├── FansActivity.java
│                           │   ├── FindPwdActivity.java
│                           │   ├── FlashOrderActivity.java
│                           │   ├── FlashOrderCancleActivity.java
│                           │   ├── FlashOrderDetailActivity.java
│                           │   ├── FollowActivity.java
│                           │   ├── FootActivity.java
│                           │   ├── InviteWebViewActivity.java
│                           │   ├── LiveClassActivity.java
│                           │   ├── LocationActivity.java
│                           │   ├── LoginActivity.java
│                           │   ├── LoginInvalidActivity.java
│                           │   ├── LogoutActivity.java
│                           │   ├── LogoutWebViewActivity.java
│                           │   ├── MainActivity.java
│                           │   ├── MyCoinActivity.java
│                           │   ├── MyGiftProfitActivity.java
│                           │   ├── MyPhotoActivity.java
│                           │   ├── MyProfitActivity.java
│                           │   ├── MySkillActivity.java
│                           │   ├── OrderAccpetDetailActivity.java
│                           │   ├── OrderAccpetDetailActivity2.java
│                           │   ├── OrderCancelActivity.java
│                           │   ├── OrderCenterActivity.java
│                           │   ├── OrderCommentActivity.java
│                           │   ├── OrderCommentActivity2.java
│                           │   ├── OrderCommentActivity3.java
│                           │   ├── OrderDetailActivity.java
│                           │   ├── OrderMakeActivity.java
│                           │   ├── OrderMsgActivity.java
│                           │   ├── OrderTakingDetailActivity.java
│                           │   ├── PublishDynamicsActivity.java
│                           │   ├── RefunDealActivity.java
│                           │   ├── RefundApplyActivity.java
│                           │   ├── RegisterActivity.java
│                           │   ├── RelatedSkillsActivity.java
│                           │   ├── SearchActivity.java
│                           │   ├── SetProfileActivity.java
│                           │   ├── SettingActivity.java
│                           │   ├── SkillHomeActivity.java
│                           │   ├── SkillUserActivity.java
│                           │   ├── SnatchHallActivity.java
│                           │   ├── UserHomeActivity.java
│                           │   ├── UserReportActivity.java
│                           │   ├── VipActivity.java
│                           │   ├── VisitActivity.java
│                           │   └── WalletActivity.java
│                           ├── adapter
│                           │   ├── AllClassAdapter.java
│                           │   ├── AllSkillAdapter.java
│                           │   ├── AuthGameAdapter.java
│                           │   ├── AuthImageAdapter.java
│                           │   ├── BlackListAdapter.java
│                           │   ├── CashAccountAdapter.java
│                           │   ├── ChatRoomMoreListAdapter.java
│                           │   ├── CoinAdapter.java
│                           │   ├── CoinPayAdapter.java
│                           │   ├── DidiOrderAdapter.java
│                           │   ├── FansAdapter.java
│                           │   ├── FollowAdapter.java
│                           │   ├── FootAdapter.java
│                           │   ├── GiftCabAdapter.java
│                           │   ├── GreatManAdapter.java
│                           │   ├── InterestAdapter.java
│                           │   ├── LetterIndexAdapter.java
│                           │   ├── LiveClassAdapter.java
│                           │   ├── LiveShareAdapter.java
│                           │   ├── LocationAdapter.java
│                           │   ├── LoginTypeAdapter.java
│                           │   ├── LogoutConditionAdapter.java
│                           │   ├── MainHomeClassAdapter.java
│                           │   ├── MainHomeFollowAdapter.java
│                           │   ├── MainHomeFollowTopUserAdapter.java
│                           │   ├── MainHomeLiveAdapter.java
│                           │   ├── MainHomeLiveClassAdapter.java
│                           │   ├── MainHomeNearAdapter.java
│                           │   ├── MainHomeRecommendAdapter.java
│                           │   ├── MainHomeRecommendHeadLiveAdapter.java
│                           │   ├── MainHomeRecommendTopAdapter.java
│                           │   ├── MainMeAdapter.java
│                           │   ├── MainMeAdapter2.java
│                           │   ├── MainPlayUserAdapter.java
│                           │   ├── MySkillAdapter.java
│                           │   ├── MyWallChooseImageAdapter.java
│                           │   ├── OrderCancelAdapter.java
│                           │   ├── OrderCenterAdapter.java
│                           │   ├── OrderMsgAdapter.java
│                           │   ├── OrderPayAdapter.java
│                           │   ├── PhoneCountryCodeAdapter.java
│                           │   ├── PhoneCountryCodeSearchAdapter.java
│                           │   ├── PhotoAdapter.java
│                           │   ├── PubDynAdapter.java
│                           │   ├── PubDynAdapter2.java
│                           │   ├── RelatedSkillsAdapter.java
│                           │   ├── SearchAdapter.java
│                           │   ├── SearchGameAdapter.java
│                           │   ├── SkillCommentAdapter.java
│                           │   ├── SkillLabelAdapter.java
│                           │   ├── SkillPriceTipAdapter.java
│                           │   ├── SkillUserAdapter.java
│                           │   ├── SnatchHallAdapter.java
│                           │   ├── UserHomeProfileAdapter.java
│                           │   ├── UserHomeSkillAdapter.java
│                           │   ├── VipBuyAdapter.java
│                           │   ├── VipItemAdapter.java
│                           │   └── VisitAdapter.java
│                           ├── bean
│                           │   ├── AllSkillBean.java
│                           │   ├── AllSkillSectionBean.java
│                           │   ├── BannerBean.java
│                           │   ├── BonusBean.java
│                           │   ├── CashAccountBean.java
│                           │   ├── CountryCodeBean.java
│                           │   ├── CountryCodeParentBean.java
│                           │   ├── DripBean.java
│                           │   ├── FootBean.java
│                           │   ├── GiftCabBean.java
│                           │   ├── GreateManBean.java
│                           │   ├── InterestBean.java
│                           │   ├── LoginTypeBean.java
│                           │   ├── LogoutConditionBean.java
│                           │   ├── MySkillBean.java
│                           │   ├── OrderCancelBean.java
│                           │   ├── OrderPayBean.java
│                           │   ├── PhotoBean.java
│                           │   ├── RecommendUserBean.java
│                           │   ├── RefundinfoBean.java
│                           │   ├── SkillClassBean.java
│                           │   ├── SkillHomeBean.java
│                           │   ├── SkillLabelBean.java
│                           │   ├── SkillLevelBean.java
│                           │   ├── SkillMyBean.java
│                           │   ├── SkillPriceBean.java
│                           │   ├── SkillPriceTipBean.java
│                           │   ├── SkillUserBean.java
│                           │   ├── SnapOrderBean.java
│                           │   ├── TagBean.java
│                           │   ├── VipBuyBean.java
│                           │   ├── VipItemBean.java
│                           │   ├── VisitBean.java
│                           │   ├── WallBean.java
│                           │   └── commit
│                           │       ├── DressingCommitBean.java
│                           │       └── FlashOrderCommitBean.java
│                           ├── business
│                           │   ├── ConditionModel.java
│                           │   └── OrderCutDownModel.java
│                           ├── custom
│                           │   ├── LinePagerIndicator.java
│                           │   ├── StarCountView.java
│                           │   ├── StartSnapHelper.java
│                           │   ├── TagGroup.java
│                           │   ├── TagGroup2.java
│                           │   ├── UploadImageView.java
│                           │   └── UploadImageView2.java
│                           ├── dialog
│                           │   ├── AgentDialogFragment.java
│                           │   ├── BuyVipDialogFragment.java
│                           │   ├── LoginTipDialogFragment.java
│                           │   ├── PayDialogFragment.java
│                           │   ├── SelectGreateManFragment.java
│                           │   ├── SelectSkillDialogFragemnt.java
│                           │   ├── SkillLabelDialogFragment.java
│                           │   ├── SkillPriceDialogFragment.java
│                           │   └── SkillPriceTipDialogFragment.java
│                           ├── event
│                           │   ├── OpenDrawEvent.java
│                           │   ├── OrderCancelEvent.java
│                           │   └── UpdateSkillEvent.java
│                           ├── http
│                           │   ├── MainHttpConsts.java
│                           │   └── MainHttpUtil.java
│                           ├── interfaces
│                           │   ├── AppBarStateListener.java
│                           │   ├── DataLoader.java
│                           │   ├── MainAppBarExpandListener.java
│                           │   ├── MainAppBarLayoutListener.java
│                           │   └── MainStartChooseCallback.java
│                           ├── presenter
│                           │   └── CheckLivePresenter.java
│                           ├── utils
│                           │   ├── CityUtil.java
│                           │   └── MainIconUtil.java
│                           └── views
│                               ├── AbsMainHomeParentParentViewHolder.java
│                               ├── AbsMainHomeParentViewHolder.java
│                               ├── AbsWalletDetailViewHolder.java
│                               ├── BonusViewHolder.java
│                               ├── CashAccountViewHolder.java
│                               ├── MainDynamicViewHolder.java
│                               ├── MainFollowDynamicViewHolder.java
│                               ├── MainHomeAttentionDynamicViewHolder.java
│                               ├── MainHomeDynamicViewHolder.java
│                               ├── MainHomeFollowViewHolder.java
│                               ├── MainHomeGameChildViewHolder.java
│                               ├── MainHomeLiveViewHolder.java
│                               ├── MainHomeNearViewHolder.java
│                               ├── MainHomeParentDynamicViewHolder.java
│                               ├── MainHomeParentPlayViewHolder.java
│                               ├── MainHomeParentViewHolder.java
│                               ├── MainHomeRecommendViewHolder.java
│                               ├── MainHomeViewHolder.java
│                               ├── MainLiveChildViewHolder.java
│                               ├── MainLiveViewHolder.java
│                               ├── MainMeViewHolder.java
│                               ├── MainMeViewHolder2.java
│                               ├── MainMessageMsgViewHolder.java
│                               ├── MainMessageViewHolder.java
│                               ├── MainNewestDynamicViewHolder.java
│                               ├── MainRecommendDynamicViewHolder.java
│                               ├── MyPhotoViewHolder.java
│                               ├── PhotoDetailViewHolder.java
│                               ├── SelectConditionViewHolder.java
│                               ├── SkillCommentViewHolder.java
│                               ├── UserHomeProfileViewHolder.java
│                               ├── UserHomeSkillViewHolder.java
│                               ├── WalletExpandViewHolder.java
│                               └── WalletIncomeViewHolder.java
├── mob
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           └── java
│               └── cn
│                   └── sharesdk
│                       └── onekeyshare
│                           ├── CustomerLogo.java
│                           ├── OnekeyShare.java
│                           ├── OnekeySharePage.java
│                           ├── OnekeyShareTheme.java
│                           ├── OnekeyShareThemeImpl.java
│                           ├── ShareContentCustomizeCallback.java
│                           └── themes
│                               └── classic
│                                   ├── ClassicTheme.java
│                                   ├── EditPage.java
│                                   ├── FriendAdapter.java
│                                   ├── FriendListItem.java
│                                   ├── FriendListPage.java
│                                   ├── IndicatorView.java
│                                   ├── PRTHeader.java
│                                   ├── PicViewerPage.java
│                                   ├── PlatformPage.java
│                                   ├── PlatformPageAdapter.java
│                                   ├── RotateImageView.java
│                                   ├── XView.java
│                                   ├── land
│                                   │   ├── EditPageLand.java
│                                   │   ├── FriendListPageLand.java
│                                   │   ├── PlatformPageAdapterLand.java
│                                   │   └── PlatformPageLand.java
│                                   └── port
│                                       ├── EditPagePort.java
│                                       ├── FriendListPagePort.java
│                                       ├── PlatformPageAdapterPort.java
│                                       └── PlatformPagePort.java
├── silicompressor
│   └── src
│       ├── androidTest
│       │   └── java
│       │       └── com
│       │           └── iceteck
│       │               └── silicompressorr
│       │                   └── ApplicationTest.java
│       ├── main
│       │   ├── AndroidManifest.xml
│       │   └── java
│       │       └── com
│       │           └── iceteck
│       │               └── silicompressorr
│       │                   ├── FileUtils.java
│       │                   ├── SiliCompressor.java
│       │                   ├── Util.java
│       │                   ├── provider
│       │                   │   └── GenericFileProvider.java
│       │                   └── videocompression
│       │                       ├── Config.java
│       │                       ├── InputSurface.java
│       │                       ├── MP4Builder.java
│       │                       ├── MediaController.java
│       │                       ├── Mp4Movie.java
│       │                       ├── OutputSurface.java
│       │                       ├── Sample.java
│       │                       ├── TextureRenderer.java
│       │                       └── Track.java
│       └── test
│           └── java
│               └── com
│                   └── iceteck
│                       └── silicompressorr
│                           └── ExampleUnitTest.java
├── test.md
├── tmpmob
│   └── ShareSDK
├── tree.txt
├── video
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           └── java
│               └── com
│                   └── example
│                       └── video
│                           ├── ui
│                           │   ├── activity
│                           │   │   └── VideoPlayActivity.java
│                           │   └── view
│                           │       ├── AbsPlayViewHolder.java
│                           │       ├── IMediaController.java
│                           │       └── IjkViewPlayer.java
│                           ├── util
│                           │   ├── GSYVideoType.java
│                           │   └── MeasureHelper.java
│                           └── widet
│                               ├── GSYTextureView.java
│                               ├── IGSYRenderView.java
│                               ├── IGSYSurfaceListener.java
│                               ├── IjkplayerVideoView_TextureView.java
│                               ├── VideoListener.java
│                               └── VideoPlayer.java
└── yunbaoyuedan.jks

253 directories, 1229 files
