# Team 9 - 301 Team

 > _Note:_ This document is meant to be written during (or shortly after) your review meeting, which should happen fairly close to the due date.      
 >      
 > _Suggestion:_ Have your review meeting a day or two before the due date. This way you will have some time to go over (and edit) this document, and all team members should have a chance to make their contribution.


## Iteration XX - Review & Retrospect

 * When: 6/25/2025
 * Where: Online on Zoom 

## Process - Reflection


#### Q1. What worked well

1. **Consistent Weekly Scrum Meetings with Structured Discussion**  
   Our team held weekly scrum meetings on Discord where each member shared what they had completed and what they planned to work on next. To stay organized, we maintained a shared Google Doc where we outlined discussion points, captured blockers, and tracked next steps. These meetings were consistently well-attended, and the structure ensured that everyone was aligned, which contributed to steady weekly progress.

2. **Effective Task Management through Jira**  
   We used Jira for sprint planning and task assignments. Each user story was broken into smaller, actionable tasks and assigned to relevant team members. This helped everyone understand the priorities for the week and maintain individual accountability. The visual tracking of story progress also made it easier to identify any bottlenecks or overlapping efforts.

3. **Smooth Code Collaboration via GitHub**  
   Our team collaborated effectively using GitHub. Pull requests, branches, and regular commits allowed us to avoid merge conflicts and review each other’s code efficiently. We followed naming conventions and maintained a clean main branch, which enabled faster integration of new features and bug fixes.

4. **Strategic Division of Responsibilities**  
   The initial assignment of roles and components (based on each member’s strengths and interests) played a major role in our productivity. Since everyone had clearly defined areas of focus—such as frontend features, backend APIs, or AI integration—we were able to work in parallel and deliver features quickly without blocking each other.

#### Q2. What did not work well

1. **Unclear Requirements from the Partner**  
   There were delays due to unclear requirements from the partner. Specifically, the database of exercises was not provided on time, which meant we couldn't complete many functionalities initially. To continue progress, we decided to use fake demo data, allowing us to proceed with development. Once the database was provided, we switched to using the real data.

2. **Environment and Setup Issues**  
   Some team members faced difficulties with running the Docker container and setting up the database correctly. This caused delays in getting the development environment up and running. After a dedicated troubleshooting session, we clarified the process and ensured that everyone understood how to properly configure and run the setup, which resolved the issue.

3. **Uncertainty Around AI Integration**  
   The AI integration component was left vague by the partner, leading to uncertainty about the specific use cases and expectations for AI-powered features (e.g., chatbot and exercise recommendations). This ambiguity caused delays as we spent extra time determining the scope of AI functionality and aligning it with our product vision.

#### Q3(a). Planned changes

1. **More Proactive Communication with the Partner**  
   To avoid delays caused by missing or unclear partner deliverables (such as the exercise database), we plan to schedule earlier and more frequent follow-ups. Our partner liaison will now request key resources at least one week in advance and confirm expectations in writing to minimize ambiguity and prevent future slowdowns.

2. **Introduce Mid-Week Async Check-Ins**  
   While our weekly meetings are effective, sometimes we discover issues too late in the week to respond quickly. To address this, we’ll begin posting short mid-week updates on Discord (e.g., Wednesday) where team members list their current task, progress status, and any problems they run into. This small process-related change will help us identify issues earlier and maintain a good workflow throughout the sprint.

#### Q3(b). Integration & Next steps

We used a single GitHub repository for the project, with each team member working in their own branch. Pull requests were made to the `main` branch, where code was reviewed and tested. Once verified, changes were merged into the `dev` branch, which served as our production-ready version. The earlier assignment structure helped us clarify responsibilities, but using a shared repo from the start made integration much smoother.

## Product - Review

#### Q4. How was your product demo?
 
 **Preparation:**  
The night before the demo, we made sure the latest version of the project on GitHub was running smoothly and ready to be shown. We prepared a checklist of implemented features and assigned one team member to lead the demo presentation. This ensured that there would be no issues when showing the partner our demo.

**Demoed Features:**  
We successfully demoed the following features to our partner:
- User sign-up and login functionality
- A profile page where users can input preferences such as age, gender, equipment available, etc. 
- An exercise log that tracks completed workouts
- An AI-powered exercise recommender that adapts to user preferences
- A chatbot interface that answers workout and injury-related questions

**Partner Feedback:**  
Our partner was happy with the progress and the quality of the features implemented so far. She especially liked the clean UI design and the logical flow between different pages. There were no major change requests, and her feedback was positive, telling us to move forward with the development. 

**Key Learnings:**  
One of the biggest takeaways from the demo process was the importance of scoping. Rather than trying to implement every possible feature, we focused on making a few core features work extremely well. For example, we limited the injury handling functionality to only the most common injury types for the MVP to ensure that we could get these functionalities to work perfectly. Our decisions on functionality scope led us to fully finish the features we needed to implement for the demo, allowing for the demo to be more cohesive and complete. 
