import React from 'react';
import { BrowserRouter as Router, Route, Switch, Redirect } from 'react-router-dom';
import HomePage from './components/HomePage';
import PlanStepperPage from './components/PlanStepperPage';
import PlanPage from './components/PlanPage';
import SummaryPage from './components/SummaryPage';
import ProfilePage from './components/ProfilePage';
import ChatbotPage from './components/ChatbotPage';
import LoginPage from './components/LoginPage';
import RegistrationPage from './components/RegistrationPage';
import ExerciseSessionPage from './components/ExerciseSessionPage';

// Simple authentication check
const isAuthenticated = () => {
  return localStorage.getItem('authToken') !== null;
};

// Protected Route component
const ProtectedRoute: React.FC<{ component: React.ComponentType<any>; path: string; exact?: boolean }> = ({ component: Component, ...rest }) => (
  <Route
    {...rest}
    render={(props) =>
      isAuthenticated() ? (
        <Component {...props} />
      ) : (
        <Redirect to="/login" />
      )
    }
  />
);

function App() {
  return (
    <Router>
      <div className="App">
        <Switch>
          <Route path="/login" component={LoginPage} />
          <Route path="/register" component={RegistrationPage} />
          <ProtectedRoute path="/home" component={HomePage} />
          <ProtectedRoute path="/plan-stepper" component={PlanStepperPage} />
          <ProtectedRoute path="/summary" component={PlanPage} />
          <ProtectedRoute path="/calendar-summary" component={SummaryPage} />
          <ProtectedRoute path="/profile" component={ProfilePage} />
          <ProtectedRoute path="/chatbot" component={ChatbotPage} />
          <ProtectedRoute path="/exercise-session" component={ExerciseSessionPage} />
          <Route exact path="/">
            {isAuthenticated() ? <Redirect to="/home" /> : <Redirect to="/login" />}
          </Route>
          <Route path="*">
            <Redirect to="/" />
          </Route>
        </Switch>
      </div>
    </Router>
  );
}

export default App;
