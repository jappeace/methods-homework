% Package `RJournal' to use with LaTeX2e
% Copyright (C) 2010 by the R Foundation
% Copyright (C) 2013 by the R Journal
% Copyleft education fair use modifications 2016
%
% Originally written by Kurt Hornik and Friedrich Leisch with subsequent 
% edits by the editorial board
%
% Modified for use in the INFOMAA course by Jonas A. Hultén. Modifications made
% under educational fair use provisions.

\NeedsTeXFormat{LaTeX2e}[1995/12/01]
\ProvidesPackage{malrep}[2016/06/09 v0.1.0 MAL Final Report Class]
\LoadClassWithOptions{report}

\RequirePackage{tikz}
\RequirePackage{multicol}
\RequirePackage[utf8]{inputenc}

% Overall page layout, fonts etc -----------------------------------------------

\RequirePackage{ifdraft}
\RequirePackage{geometry}
\geometry{a4paper, 
   textwidth=14cm, top=1cm, bottom=1cm,
   includehead,includefoot,centering,
   footskip=1.5cm}
\raggedbottom

\RequirePackage{fancyhdr}
\setlength{\headheight}{14pt}
\fancyhead{}
\fancyheadoffset{2cm} 
\fancyhead[RE,LO]{\textsc{\MAL@headertitle}}
\fancyhead[C]{\ifdraft{\textsc{Draft \MAL@draftnum}}{}}
\fancyhead[LE,RO]{\thepage}
\fancyfoot{}
\fancyfoot[LE,RO]{Submitted on \MAL@submitted}
\fancyfoot[RE,LO]{\MAL@footauthor}
\pagestyle{fancy}

% We use the following fonts (all with T1 encoding):
% 
%   rm   & palatino
%   tt   & inconsolata
%   sf   & helvetica
%   math & palatino

\RequirePackage{microtype}

\RequirePackage[scaled=0.92]{helvet}
\RequirePackage{palatino,mathpazo}
\RequirePackage[scaled=1.02]{inconsolata}
\RequirePackage[T1]{fontenc}

\RequirePackage[hyphens]{url}
\RequirePackage[pagebackref]{hyperref}
\renewcommand{\backref}[1]{[p#1]}

% Give the text a little room to breath
\setlength{\parskip}{3pt}
\RequirePackage{setspace}
\setstretch{1.05}

% Issue and article metadata ---------------------------------------------------

% Individual articles correspond to
% chapters, and are contained in |article| environments.  This makes it
% easy to have figures counted within articles and hence hyperlinked
% correctly.

% An article has an author, a title, and optionally a subtitle.  We use
% the obvious commands for specifying these. Articles will be put in certain
% journal sections, named by \sectionhead.

\newcommand  {\headertitle}  [1]{\def\MAL@headertitle{#1}}
\renewcommand{\author}       [1]{\def\MAL@author{#1}}
\renewcommand{\title}        [1]{\def\MAL@title{#1}}
\newcommand  {\subtitle}     [1]{\def\MAL@subtitle{#1}}
\newcommand  {\footauthor}   [1]{\def\MAL@footauthor{#1}}
\newcommand  {\submitted}    [1]{\def\MAL@submitted{#1}}
\newcommand  {\draftnum}     [1]{\def\MAL@draftnum{#1}}

% Control appearance of titles: make slightly smaller than usual, and 
% suppress section numbering. See http://tex.stackexchange.com/questions/69749
% for why we don't use \setcounter{secnumdepth}{-1} 

\usepackage[medium]{titlesec}
\usepackage{titletoc}
\titleformat{\section}   {\normalfont\large\bfseries}{}{0em}{}
\titleformat{\subsection}{\normalfont\normalsize\bfseries}{}{0em}{}
\titlecontents{chapter}   [0em]{}{}{}{\titlerule*[1em]{.}\contentspage}

% Article layout ---------------------------------------------------------------

% Environment |article| clears the article header information at its beginning. 
% We use |\FloatBarrier| from the placeins package to keep floats within
% the article.
\RequirePackage{placeins}
\newenvironment{article}{\author{}\title{}\subtitle{}\FloatBarrier%
  \begin{multicols}{2}}%
  {\end{multicols}\FloatBarrier}

% Refereed articles should have an abstract, so we redefine |\abstract| to
% give the desired style.
% Not reverting a \small causes headaches. Lesson learned.

\renewcommand{\abstract}[1]{%
\setstretch{1}%
\noindent%
\small%
\textbf{Abstract} #1
\normalsize
}

% Multicols breaking \multicolinterrupt command. I'm sure fun stuff can be made
% with this.
\newcounter{tempcolnum}
\newcommand{\multicolinterrupt}[1]{% Column spanning!
\setcounter{tempcolnum}{\col@number}
\end{multicols}
#1%
\begin{multicols}{\value{tempcolnum}}
}

% The real work is done by a redefined version of |\maketitle|.  Note
% that even though we do not want chapters (articles) numbered, we
% need to increment the chapter counter, so that figures get correct
% labelling.

\renewcommand{\maketitle}{%
\noindent
  \chapter{\MAL@title}\refstepcounter{chapter}
  \ifx\empty\MAL@subtitle
  \else
    \noindent\textbf{\MAL@subtitle}
    \par\nobreak\addvspace{\baselineskip}
  \fi
  \ifx\empty\MAL@author
  \else
    \noindent\textit{\MAL@author}
    \par\nobreak\addvspace{\baselineskip}
  \fi
  \@afterindentfalse\@nobreaktrue\@afterheading
}

% Now for some ugly redefinitions.  We do not want articles to start a
% new page. (Actually, we do, but this is handled via explicit
% \newpage
%
% The name@of@eq is a hack to get hyperlinks to equations to work
% within each article, even though there may be multiple eq.(1)
%    \begin{macrocode}
\renewcommand\chapter{\secdef\MAL@chapter\@schapter}
\providecommand{\nohyphens}{%
  \hyphenpenalty=10000\exhyphenpenalty=10000\relax}
\newcommand{\MAL@chapter}{%
  \edef\name@of@eq{equation.\@arabic{\c@chapter}}%
  \renewcommand{\@seccntformat}[1]{}%
  \@startsection{chapter}{0}{0mm}{%
    -2\baselineskip \@plus -\baselineskip \@minus -.2ex}{\p@}{%
    \phantomsection\normalfont\huge\bfseries\raggedright}}

% We want bibliographies as starred sections within articles.
% 
\RequirePackage[sectionbib,round]{natbib}
\bibliographystyle{abbrvnat}

% Equations, figures and tables are counted within articles, but we do
% not show the article number. For equations it becomes a bit messy to avoid
% having hyperref getting it wrong. 

% \numberwithin{equation}{chapter}
\renewcommand{\theequation}{\@arabic\c@equation}
\renewcommand{\thefigure}{\@arabic\c@figure}
\renewcommand{\thetable}{\@arabic\c@table}

% Text formatting --------------------------------------------------------------

\newcommand{\address}[1]{\addvspace{\baselineskip}\noindent\emph{#1}}
\newcommand{\email}[1]{\href{mailto:#1}{\normalfont\texttt{#1}}}

% Simple font selection is not good enough.  For example, |\texttt{--}|
% gives `\texttt{--}', i.e., an endash in typewriter font.  Hence, we
% need to turn off ligatures, which currently only happens for commands
% |\code| and |\samp| and the ones derived from them.  Hyphenation is
% another issue; it should really be turned off inside |\samp|.  And
% most importantly, \LaTeX{} special characters are a nightmare.  E.g.,
% one needs |\~{}| to produce a tilde in a file name marked by |\file|.
% Perhaps a few years ago, most users would have agreed that this may be
% unfortunate but should not be changed to ensure consistency.  But with
% the advent of the WWW and the need for getting `|~|' and `|#|' into
% URLs, commands which only treat the escape and grouping characters
% specially have gained acceptance

\DeclareRobustCommand\code{\bgroup\@noligs\@codex}
\def\@codex#1{\texorpdfstring%
{{\normalfont\ttfamily\hyphenchar\font=-1 #1}}%
{#1}\egroup}
\newcommand{\kbd}[1]{{\normalfont\texttt{#1}}}
\newcommand{\key}[1]{{\normalfont\texttt{\uppercase{#1}}}}
\DeclareRobustCommand\samp{`\bgroup\@noligs\@sampx}
\def\@sampx#1{{\normalfont\texttt{#1}}\egroup'}
\newcommand{\var}[1]{{\normalfont\textsl{#1}}}
\let\env=\code
\newcommand{\file}[1]{{`\normalfont\textsf{#1}'}}
\let\command=\code
\let\option=\samp
\newcommand{\dfn}[1]{{\normalfont\textsl{#1}}}
\newcommand{\strong}[1]{\texorpdfstring%
{{\normalfont\fontseries{b}\selectfont #1}}%
{#1}}
\let\pkg=\strong

% Mathematics ------------------------------------------------------------------

% The implementation of |\operatorname| is similar to the mechanism
% \LaTeXe{} uses for functions like sin and cos, and simpler than the
% one of \AmSLaTeX{}.  We use |\providecommand| for the definition in
% order to keep the one of the \pkg{amstex} if this package has
% already been loaded.
%    \begin{macrocode}
\providecommand{\operatorname}[1]{%
  \mathop{\operator@font#1}\nolimits}
\RequirePackage{amsfonts}

\renewcommand{\P}{%
  \mathop{\operator@font I\hspace{-1.5pt}P\hspace{.13pt}}}
\newcommand{\E}{%
  \mathop{\operator@font I\hspace{-1.5pt}E\hspace{.13pt}}}
\newcommand{\VAR}{\operatorname{var}}
\newcommand{\COV}{\operatorname{cov}}
\newcommand{\COR}{\operatorname{cor}}

% Figures ----------------------------------------------------------------------

\RequirePackage[font=small,labelfont=bf]{caption}

% Wide environments for figures and tables -------------------------------------
\RequirePackage{environ}

% An easy way to make a figure span the full width of the page
\NewEnviron{widefigure}[1][]{
\begin{figure}[#1]
\advance\leftskip-2cm
\begin{minipage}{\dimexpr\textwidth+4cm\relax}%
  \captionsetup{margin=2cm}
  \BODY
\end{minipage}%
\end{figure}
}

\NewEnviron{widetable}[1][]{
\begin{table}[#1]
\advance\leftskip-2cm
\begin{minipage}{\dimexpr\textwidth+4cm\relax}%
  \captionsetup{margin=2cm}
  \BODY
\end{minipage}%
\end{table}
}
