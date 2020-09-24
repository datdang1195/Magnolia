[#assign isAnonymous = model.isAnonymousRole()]
[#assign onJobTraining = model.getOnJobTrainingData()!]
[#assign user = model.getCurrentUser()!]
[#assign supervisorRole = content.supervisorRole!"academy-supervisor-role"]
[#assign participantRole = content.participantRole!"academy-user-role"]
[#assign titleInfo = model.getTitle(onJobTraining.getProgram()!)!]
[#assign isOJTParticipant = model.isOJTParticipant()]

[#if user.getAllRoles()?seq_contains(supervisorRole) || isOJTParticipant]
    [#if onJobTraining?has_content]
        [#if user.getAllRoles()?seq_contains(supervisorRole) || user.getAllRoles()?seq_contains(participantRole)]
            <div class="app-wrapper__onJobTraining">
                <div id="loading" class="loading">
                    <div class="loading__content">
                        <i></i>
                    </div>
                </div>
                <div class="container-onJobTraining">
                    <div class="onJobTraining">
                        <p class="page__title">On the job training</p>
                        [#if titleInfo?has_content && titleInfo?size == 2]
                            <div class="wrapper-title">
                                <p class="phase-title">${titleInfo[0]}</p>
                                <p class="line">&nbsp;&dash;&nbsp;</p>
                                <p class="course-group">${titleInfo[1]}</p>
                            </div>
                        [/#if]
                    </div>
                    <div class="content-onJobTraining">
                        <div class="onJobTraining-project onJobTraining-group">
                            <p class="group__label">Project name&#58;</p>
                            <p class="group__value">${onJobTraining.getProjectName()!}</p>
                        </div>
                        <div class="wrapper-section">
                            <div class="onJobTraining-status onJobTraining-group">
                                <p class="group__label">Project status&#58;</p>
                                <p class="group__value">${onJobTraining.getProjectStatus().getDisplayName()!}</p>
                            </div>
                            <div class="onJobTraining-lead onJobTraining-group">
                                <p class="group__label">Project Manager&#58;</p>
                                <p class="group__value">${onJobTraining.getProjectLead().getFullName()!}</p>
                            </div>
                        </div>
                        <div class="wrapper-section">
                            <div class="onJobTraining-startDay onJobTraining-group">
                                <p class="group__label">Start date&#58;</p>
                                <p class="group__value">${onJobTraining.getStartDate()!}</p>
                            </div>
                            <div class="onJobTraining-endDay onJobTraining-group">
                                <p class="group__label">End date&#58;</p>
                                <p class="group__value">${onJobTraining.getEndDate()!}</p>
                            </div>
                        </div>
                        <div class="onJobTraining-description">
                            <p class="description__label">Description&#58;</p>
                            <div class="description__value">${onJobTraining.getDescription()!}</div>
                        </div>
                        <div class="list-participants">
                            <p class="participant__label">Participants&#58;</p>

                            [#if user.getAllRoles()?seq_contains(supervisorRole)]
                                <div class="participant__content table-wrapper role-supervisor">
                                    <table class="table has-mobile-cards">
                                        <tr>
                                            <th>Name</th>
                                            <th>Role</th>
                                            <th>Supervisor</th>
                                            <th>Score</th>
                                            <th>Comment</th>
                                        </tr>
                                        [#list onJobTraining.getParticipants() as participant]
                                            <tr>
                                                <td data-label="Name">${participant.getName()!}</td>
                                                <td data-label="Role">${participant.getRole().getDisplayName()!}</td>
                                                <td data-label="Supervisor">${participant.getMentor()!}</td>
                                                [#if participant.getScore()! == 0]
                                                    <td data-label="Score">-</td>
                                                [#else ]
                                                    <td data-label="Score">${participant.getScore()!"-"}</td>
                                                [/#if]

                                                <td data-label="Comment">${participant.getComment()!"-/-"}</td>
                                            </tr>
                                        [/#list]
                                    </table>
                                </div>
                            [#elseif user.getAllRoles()?seq_contains(participantRole)]
                                <div class="participant__content table-wrapper role-participants">
                                    <table class="table has-mobile-cards">
                                        <tr>
                                            <th>Name</th>
                                            <th>Role</th>
                                            <th>Mentor</th>
                                            <th>Note</th>
                                        </tr>
                                        [#list onJobTraining.getParticipants() as participant]
                                            <tr>
                                                <td data-label="Name">${participant.getName()!}</td>
                                                <td data-label="Role">${participant.getRole().getDisplayName()!}</td>
                                                <td data-label="Mentor">${participant.getMentor()!}</td>
                                                <td data-label="Note">${participant.getNote()!}</td>
                                            </tr>
                                        [/#list]

                                    </table>
                                </div>
                            [/#if]
                        </div>
                    </div>
                </div>
                <div class="goToTopPage">
                    <div class="wrapper-goTop">
                        <img src="${ctx.contextPath}/.resources/cert-checker-module/webresources/images/goToTop.svg"
                             class="icon-goTop" alt="go to top page">
                        <p class="goTo__title">Top</p>
                    </div>
                </div>
            </div>
        [#else ]
            <h2>Oops! you cannot access this page!</h2>
        [/#if]
    [#else ]
        <h2>On job training is empty</h2>
    [/#if]

    [#else]
        [#if !isAnonymous]
            ${ctx.response.sendRedirect("/unauthorized-access")!}
        [/#if]
[/#if]