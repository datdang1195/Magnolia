$(document).ready(function () {
  // main input
  $('.js-btn-main-cancel').click(function() {
    const $btnCancel = $(this);
    const $mainBtnGroup = $btnCancel.closest('.js-comment-main-btn');
    $mainBtnGroup.removeClass('show');
  });

  $('.js-comment-main-input').click(function() {
    $(this).find('.js-comment-text-area').focus();
  });

  $('.js-comment-text-area').focus(function() {
    const $textArea = $(this);
    const $mainComment = $textArea.closest('.js-comment-main-thread');
    const $mainBtnGroup = $mainComment.find('.js-comment-main-btn');
    $mainBtnGroup.addClass('show');
  });


  // send comment
  $('.js-btn-main-send').click(function () {

    const $btnSendComment = $(this);
    let dataObj = {
      userId: window.userProfile.userId,
      comment: $('.js-comment-text-area').val(),
      level: 1,
      courseId: courseId
    }
    $.ajax({
      type: 'POST',
      url: '/.rest/certChecker/v1/comment',
      data: JSON.stringify(dataObj),
      headers: {
        'Content-Type':'application/json'
      },
      success: function(data){
        let $countComment  = $btnSendComment.closest('.js-comment-main-thread').parent('.comment__flow').siblings('.justify-content-center').find('#js-comment-count-number').html();
        let $totalCount = ++$countComment;
        $('#js-comment-count-number').html('');
        $('#js-comment-count-number').append($totalCount);

        const $mainBtnGroup = $btnSendComment.closest('.js-comment-main-btn');
        $mainBtnGroup.removeClass('show');
        const renderComment = `<div class="comment__post__item js-comment-post-item">
                                <input style="display: none" class="js-id-root-comment" value=${data.id}>
                                <div class="evn-row">
                                    <div class="comment__photo"><img src="${window.userProfile.photoUrl}" alt="" class="comment__big-photo"></div>
                                    <div class="comment__data">
                                        <div class="comment__name">${window.userProfile.fullName}</div>
                                        <div class="comment__time">just now</div>
                                    </div>
                                </div>
                                <div class="comment__text">
                                    <div class="comment__text__data">${dataObj.comment}</div>
                                    <div class="comment__data__group evn-row js-comment-data-group">
                                        <div class="comment__count__group evn-row js-comment-count">
                                            <div class="comment__count__figure">
                                                <img src="${contextPath}/.resources/cert-checker-module/webresources/images/icon-comment.png" alt="" class="comment__icon">
                                            </div>
                                            <div class="comment__text__count comment__text__count--is-positive js-comment-reply-text-area">0</div>
                                        </div>
                                        <div class="btn-comment-reply js-btn-comment-reply">REPLY</div>
                                    </div>
                                    <div class="js-list-reply-comment"></div>
                                </div>
                                </div>`;
        $('.js-root-comment-list').append(renderComment);
      },
      error: function () {
        alert('Chức năng này chưa khả dụng');
      }
    });

  });

  // send reply comment
  $(document).on('click', '.js-comment-btn-send', function () {
    const $btnSendReplyComment = $(this);
    let $countReplyComment  = $btnSendReplyComment.closest('.js-root-comment-list') .parent('.comment__flow').siblings('.justify-content-center').find('#js-comment-count-number').html();
    let $totalCount = ++$countReplyComment;
    $('#js-comment-count-number').html('');
    $('#js-comment-count-number').append($totalCount);
    const $elmClick = $(this);
    let parentId = $elmClick.closest('.js-comment-post-item').find('.js-id-root-comment').val();
    let dataObj = {
      userId: window.userProfile.userId,
      comment: $('.js-comment-small-text-area').val(),
      level: 2,
      courseId: courseId,
      parentId: parentId
    }
    $.ajax({
      type: 'POST',
      url: '/.rest/certChecker/v1/comment',
      data: JSON.stringify(dataObj),
      headers: {
        'Content-Type':'application/json'
      },
      success: function () {
        const renderReplyComment =
            `<div class="comment__post__list js-comment-post-list">
            <div class="comment__post__item js-sub-comment-post-item">
                <div class="evn-row">
                    <div class="comment__photo"><img src="${window.userProfile.photoUrl}" alt="" class="comment__big-photo"></div>
                    <div class="comment__data">
                        <div class="comment__name">${window.userProfile.fullName}</div>
                        <div class="comment__time">just now</div>
                    </div>
                </div>
                <div class="comment__text js-comment-text">
                    <div class="comment__text__data">${dataObj.comment}</div>
                </div>
            </div>
            </div>`;
        const $replyComment = $elmClick.closest('.js-comment-toggle').siblings('.js-list-reply-comment');
        $replyComment.append(renderReplyComment);
        const $listReply = $replyComment.find('.js-comment-post-list');
        const $amontReply = $replyComment.siblings('.js-comment-data-group').find('.js-comment-reply-text-area');
        $amontReply.html($listReply.length);
        $elmClick.closest('.js-comment-toggle').remove();
      },
      error: function () {
        alert('Chức năng này chưa khả dụng');
      }
    })

  });

  // sub input
  $(document).on('click', '.js-btn-comment-reply', function() {
    const $currentReplyBtn = $(this);
    const $commentGroup = $currentReplyBtn.closest('.js-comment-data-group');
    if(!$commentGroup.next('.js-comment-toggle').length) {
      $commentGroup.after(`<div class="comment__toggle js-comment-toggle">
              <div class="comment__sub-input evn-row js-comment-sub-input">
                  <div class="comment__small-photo"><img src=${window.userProfile.photoUrl} alt="" class="comment__small-photo"></div>
                  <div class="comment__input-area">
                      <input class="comment__small-text-area js-comment-small-text-area" placeholder="Write a comment…" contenteditable type="comment__text-area">
                  </div>
              </div>
              <div class="comment-main-btn-reply comment__btn-group evn-row">
                  <button class="evn-btn evn-btn--comment js-comment-btn-cancel">CANCEL</button><button class="evn-btn evn-btn--comment js-comment-btn-send">SEND</button>
              </div>
          </div>`);
    }
    $commentGroup.next('.js-comment-toggle').find('.js-comment-small-text-area').focus();
  });

  $('.js-comment-text-area').keyup(function(e){
    if(e.keyCode === 13){
      $('.js-btn-main-send').click();
    }
  })

  $(document).on('keyup', '.js-comment-small-text-area',function(e){
    if(e.keyCode === 13){
      $('.js-comment-btn-send').click();
    }
  })

  // cancel comment
  $('.js-btn-main-cancel').click(function () {
    const $btnCancel = $(this);
    const $textArea = $btnCancel.closest('.js-comment-main-thread').find('.js-comment-text-area');
    $textArea.html('');
  });

  $(document).on('click', '.js-comment-sub-input',function() {
    $(this).find('.js-comment-small-text-area').focus();
  });

  $(document).on('click','.js-comment-btn-cancel', function() {
    $(this).closest('.js-comment-toggle').remove();
  });

  $(document).on('click','.js-btn-main-send', function() {
    $('.js-comment-text-area').val('');
  });

  $(document).on('click', '.js-comment-count',function() {
    const $parent = $(this).parent();
    const $replyComment = $parent.siblings('.js-list-reply-comment');
    $replyComment.fadeToggle(400);
  });
});